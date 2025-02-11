package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.catalina.Loader;
import org.apache.commons.lang3.RandomStringUtils;
import org.dataloader.BatchLoaderWithContext;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import fi.digitraffic.graphql.rail.querydsl.OrderByExpressionBuilder;
import fi.digitraffic.graphql.rail.querydsl.WhereExpressionBuilder;
import graphql.execution.AbortExecutionException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.QueryTimeoutException;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildFieldType> {
    private static final ThreadPoolExecutor executor = new MdcAwareThreadPoolExecutor(20);
    private static final ThreadPoolExecutor sqlExecutor = new MdcAwareThreadPoolExecutor(10);

    private static final Logger log = LoggerFactory.getLogger(BaseLink.class);

    @Value("${digitraffic.batch-load-size:500}")
    private Integer BATCH_LOAD_SIZE;

    @Autowired
    private WhereExpressionBuilder whereExpressionBuilder;

    @Autowired
    private OrderByExpressionBuilder orderByExpressionBuilder;

    public boolean cachingEnabled() {
        return true;
    }

    public abstract String getTypeName();

    public abstract String getFieldName();

    public String createDataLoaderKey() {
        return this.getTypeName() + "." + this.getFieldName();
    }

    public abstract KeyType createKeyFromParent(final ParentTOType parent);

    public abstract KeyType createKeyFromChild(final ChildTOType child);

    public abstract ChildTOType createChildTOFromTuple(final Tuple tuple);

    public abstract BatchLoaderWithContext<KeyType, ChildFieldType> createLoader();

    public abstract Class<ChildEntityType> getEntityClass();

    public abstract Expression[] getFields();

    public abstract EntityPath getEntityTable();

    public abstract BooleanExpression createWhere(final List<KeyType> keys);

    public OrderSpecifier createDefaultOrder() {
        return null;
    }

    @PersistenceContext
    private EntityManager entityManager;

    protected JPAQueryFactory queryFactory;

    @PostConstruct
    public void setup() {
        queryFactory = new JPAQueryFactory(entityManager);
    }

    public DataFetcher<CompletableFuture<ChildFieldType>> createFetcher() {
        return dataFetchingEnvironment -> {
            final ParentTOType parent = dataFetchingEnvironment.getSource();

            final DataLoaderRegistry dataLoaderRegistry = dataFetchingEnvironment.getDataLoaderRegistry();
            final DataLoader<KeyType, ChildFieldType> dataloader = dataLoaderRegistry.getDataLoader(getTypeName() + "." + getFieldName());

            return dataloader.load(createKeyFromParent(parent), dataFetchingEnvironment);
        };
    }

    private record LoaderBatch<KeyType>(List<KeyType> keys, DataFetchingEnvironment dfe) {};

    private List<LoaderBatch<KeyType>> createBatches(final List<KeyType> keys, final List<Object> keyContextsList) {
        final var batches = new ArrayList<LoaderBatch<KeyType>>();
        final var other = new ArrayList<LoaderBatch<KeyType>>();

        // make new batches for those that have alias
        // and collect all others to one list
        for(int i = 0; i < keys.size(); i++) {
            final var key = keys.get(i);
            final var dfe = (DataFetchingEnvironment) keyContextsList.get(i);

            if(dfe.getMergedField().getSingleField().getAlias() != null) {
                batches.add(new LoaderBatch<>(List.of(key), dfe));
            } else {
                if(other.isEmpty()) {
                    other.add(new LoaderBatch<>(new ArrayList<>(), dfe));
                }

                other.get(0).keys.add(key);
            }
        }

        // then partition the list with required size
        if(!other.isEmpty()) {
            final List<List<KeyType>> partitions = Lists.partition(other.get(0).keys, BATCH_LOAD_SIZE);
            batches.addAll(partitions.stream().map(p -> new LoaderBatch<>(p, other.get(0).dfe)).toList());
        }

        return batches;
    }

    protected <ResultType> BatchLoaderWithContext<KeyType, ResultType> createDataLoader(
            final BiFunction<List<ChildTOType>, DataFetchingEnvironment, Map<KeyType, ResultType>> childGroupFunction,
            final Function<JPAQueryFactory, JPAQuery<Tuple>> queryAfterFromFunction) {
        return (keys, loaderContext) -> {
            final var batches = createBatches(keys, loaderContext.getKeyContextsList());
            final boolean anyAlias = batches.stream().anyMatch(b -> b.dfe.getMergedField().getSingleField().getAlias() != null);

            return CompletableFuture.supplyAsync(() -> {
                final var childMap = new HashMap<KeyType, ResultType>(keys.size());
                final var children = new ArrayList<ResultType>(keys.size());

                final Class<ChildEntityType> entityClass = getEntityClass();
                final PathBuilder<ChildEntityType> pathBuilder = new PathBuilder<>(entityClass,
                        entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1));

                final List<Future<Map<KeyType, ResultType>>> futures = new ArrayList<>();

                for (final LoaderBatch<KeyType> batch : batches) {
                    MDC.put("execution_id", batch.dfe.getExecutionId().toString());

                    final BooleanExpression basicWhere = BaseLink.this.createWhere(batch.keys);
                    final JPAQuery<Tuple> queryAfterFrom = queryAfterFromFunction.apply(queryFactory);
                    final JPAQuery<Tuple> queryAfterWhere =
                            createWhereQuery(queryAfterFrom, pathBuilder, basicWhere, batch.dfe.getArgument("where"));
                    final JPAQuery<Tuple> queryAfterOrderBy =
                            createOrderByQuery(queryAfterWhere, pathBuilder, batch.dfe.getArgument("orderBy"));

                    futures.add(sqlExecutor.submit(
                            () -> childGroupFunction.apply(
                                    queryAfterOrderBy.fetch().stream()
                                    .map(BaseLink.this::createChildTOFromTuple)
                                    .toList()
                            , batch.dfe)));
                }

                for (final Future<Map<KeyType, ResultType>> future : futures) {
                    try {
                        final var map = future.get();
                        childMap.putAll(map);
                        children.addAll(map.values());
                    } catch (final QueryTimeoutException e) {
                        log.info("Timeout fetching children", e);
                        throw new AbortExecutionException(e);
                    } catch (final Exception e) {
                        log.error("Exception fetching children", e);
                        throw new AbortExecutionException(e);
                    }
                }

                // if there are any alises, return "children", otherwise use keys
                // (aliases have the same key, and won't work correctly), on the other hand
                // order might be wrong and the "keys" have the correct ordering
                if(anyAlias) {
                    return children;
                }

                return keys.stream().map(childMap::get).toList();
            }, executor);
        };
    }

    private JPAQuery<Tuple> createWhereQuery(final JPAQuery<Tuple> query, final PathBuilder root, final BooleanExpression basicWhere,
                                             final Map<String, Object> whereAsMap) {
        if (whereAsMap != null) {
            final BooleanExpression whereExpression = whereExpressionBuilder.create(null, root, whereAsMap);
            return query.where(basicWhere.and(whereExpression));
        } else {
            return query.where(basicWhere);
        }
    }

    private JPAQuery<Tuple> createOrderByQuery(JPAQuery<Tuple> query, final PathBuilder root, final List<Map<String, Object>> orderByArgument) {
        if (orderByArgument != null) {
            final List<OrderSpecifier> orderSpecifiers = this.orderByExpressionBuilder.create(root, orderByArgument);
            for (final OrderSpecifier orderSpecifier : orderSpecifiers) {
                query = query.orderBy(orderSpecifier);
            }
            return query;
        } else {
            final OrderSpecifier defaultOrder = this.createDefaultOrder();
            if (defaultOrder != null) {
                return query.orderBy(defaultOrder);
            } else {
                return query;
            }
        }
    }
}
