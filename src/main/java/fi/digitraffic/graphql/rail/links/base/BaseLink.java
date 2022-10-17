package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dataloader.BatchLoaderWithContext;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
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
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public abstract class BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildFieldType> {
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20, r -> new Thread(r, "baselink"));

    @Autowired
    private WhereExpressionBuilder whereExpressionBuilder;

    @Autowired
    private OrderByExpressionBuilder orderByExpressionBuilder;

    public abstract String getTypeName();

    public abstract String getFieldName();

    public abstract KeyType createKeyFromParent(ParentTOType parent);

    public abstract KeyType createKeyFromChild(ChildTOType child);

    public abstract ChildTOType createChildTOFromTuple(Tuple tuple);

    public abstract BatchLoaderWithContext<KeyType, ChildFieldType> createLoader();

    public abstract Class<ChildEntityType> getEntityClass();

    public abstract Expression[] getFields();

    public abstract EntityPath getEntityTable();

    public abstract BooleanExpression createWhere(List<KeyType> keys);

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
            ParentTOType parent = dataFetchingEnvironment.getSource();

            DataLoaderRegistry dataLoaderRegistry = dataFetchingEnvironment.getDataLoaderRegistry();
            DataLoader<KeyType, ChildFieldType> dataloader = dataLoaderRegistry.getDataLoader(getTypeName() + "." + getFieldName());

            return dataloader.load(createKeyFromParent(parent), dataFetchingEnvironment);
        };
    }

    protected <ResultType> BatchLoaderWithContext<KeyType, ResultType> createDataLoader(BiFunction<List<ChildTOType>, DataFetchingEnvironment, Map<KeyType, ResultType>> childGroupFunction) {
        BatchLoaderWithContext<KeyType, ResultType> batchLoaderWithCtx = (keys, loaderContext) -> {
            DataFetchingEnvironment dataFetchingEnvironment = (DataFetchingEnvironment) loaderContext.getKeyContextsList().get(0);

            return CompletableFuture.supplyAsync(() -> {
                List<List<KeyType>> partitions = Lists.partition(keys, 2499);
                List<ChildTOType> children = new ArrayList<>(keys.size());

                Class<ChildEntityType> entityClass = getEntityClass();
                PathBuilder<ChildEntityType> pathBuilder = new PathBuilder<>(entityClass, entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1));

                for (List<KeyType> partition : partitions) {
                    JPAQuery<Tuple> queryAfterFrom = queryFactory.select(getFields()).from(getEntityTable());
                    BooleanExpression basicWhere = BaseLink.this.createWhere(partition);
                    JPAQuery<Tuple> queryAfterWhere = createWhereQuery(queryAfterFrom, pathBuilder, basicWhere, dataFetchingEnvironment.getArgument("where"));
                    JPAQuery<Tuple> queryAfterOrderBy = createOrderByQuery(queryAfterWhere, pathBuilder, dataFetchingEnvironment.getArgument("orderBy"));

                    children.addAll(queryAfterOrderBy.fetch().stream().map(s -> BaseLink.this.createChildTOFromTuple(s)).collect(Collectors.toList()));
                }

                Map<KeyType, ResultType> childrenGroupedBy = childGroupFunction.apply(children, dataFetchingEnvironment);

                return keys.stream().map(s -> childrenGroupedBy.get(s)).collect(Collectors.toList());
            }, executor);
        };


        return batchLoaderWithCtx;
    }

    private JPAQuery<Tuple> createWhereQuery(JPAQuery<Tuple> query, PathBuilder root, BooleanExpression basicWhere, Map<String, Object> whereAsMap) {
        if (whereAsMap != null) {
            BooleanExpression whereExpression = whereExpressionBuilder.create(null, root, whereAsMap);
            return query.where(basicWhere.and(whereExpression));
        } else {
            return query.where(basicWhere);
        }
    }

    private JPAQuery<Tuple> createOrderByQuery(JPAQuery<Tuple> query, PathBuilder root, List<Map<String, Object>> orderByArgument) {
        if (orderByArgument != null) {
            List<OrderSpecifier> orderSpecifiers = this.orderByExpressionBuilder.create(root, orderByArgument);
            for (OrderSpecifier orderSpecifier : orderSpecifiers) {
                query = query.orderBy(orderSpecifier);
            }
            return query;
        } else {
            OrderSpecifier defaultOrder = this.createDefaultOrder();
            if (defaultOrder != null) {
                return query.orderBy(defaultOrder);
            } else {
                return query;
            }
        }
    }
}
