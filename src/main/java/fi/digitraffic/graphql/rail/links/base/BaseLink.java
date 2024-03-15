package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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

public abstract class BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildFieldType> {
    private static ThreadPoolExecutor executor = new MdcAwareThreadPoolExecutor(20);
    private static ThreadPoolExecutor sqlExecutor = new MdcAwareThreadPoolExecutor(10);

    private static Logger log = LoggerFactory.getLogger(BaseLink.class);

    @Autowired
    private WhereExpressionBuilder whereExpressionBuilder;

    @Autowired
    private OrderByExpressionBuilder orderByExpressionBuilder;

    public abstract String getTypeName();

    public abstract String getFieldName();

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

    private JPAQueryFactory queryFactory;

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

    protected <ResultType> BatchLoaderWithContext<KeyType, ResultType> createDataLoader(final BiFunction<List<ChildTOType>, DataFetchingEnvironment, Map<KeyType, ResultType>> childGroupFunction) {
        final BatchLoaderWithContext<KeyType, ResultType> batchLoaderWithCtx = (keys, loaderContext) -> {
            final DataFetchingEnvironment dataFetchingEnvironment = (DataFetchingEnvironment) loaderContext.getKeyContextsList().get(0);

            return CompletableFuture.supplyAsync(() -> {
                MDC.put("execution_id", dataFetchingEnvironment.getExecutionId().toString());

                final List<List<KeyType>> partitions = Lists.partition(keys, 500);
                final List<ChildTOType> children = new ArrayList<>(keys.size());

                final Class<ChildEntityType> entityClass = getEntityClass();
                final PathBuilder<ChildEntityType> pathBuilder = new PathBuilder<>(entityClass, entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1));

                final List<Future<List<ChildTOType>>> futures = new ArrayList<>();
                for (final List<KeyType> partition : partitions) {
                    final JPAQuery<Tuple> queryAfterFrom = queryFactory.select(getFields()).from(getEntityTable());
                    final BooleanExpression basicWhere = BaseLink.this.createWhere(partition);
                    final JPAQuery<Tuple> queryAfterWhere = createWhereQuery(queryAfterFrom, pathBuilder, basicWhere, dataFetchingEnvironment.getArgument("where"));
                    final JPAQuery<Tuple> queryAfterOrderBy = createOrderByQuery(queryAfterWhere, pathBuilder, dataFetchingEnvironment.getArgument("orderBy"));

                    futures.add(sqlExecutor.submit(() -> queryAfterOrderBy.fetch().stream().map(s -> BaseLink.this.createChildTOFromTuple(s)).collect(Collectors.toList())));
                }

                for (final Future<List<ChildTOType>> future : futures) {
                    try {
                        children.addAll(future.get());
                    } catch (QueryTimeoutException e) {
                        log.info("Timeout fetching children", e);
                        throw new AbortExecutionException(e);
                    } catch (Exception e) {
                        log.error("Exception fetching children", e);
                        throw new AbortExecutionException(e);
                    }
                }

                final Map<KeyType, ResultType> childrenGroupedBy = childGroupFunction.apply(children, dataFetchingEnvironment);

                return keys.stream().map(s -> childrenGroupedBy.get(s)).collect(Collectors.toList());
            }, executor);
        };


        return batchLoaderWithCtx;
    }

    private JPAQuery<Tuple> createWhereQuery(final JPAQuery<Tuple> query, final PathBuilder root, final BooleanExpression basicWhere, final Map<String, Object> whereAsMap) {
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
