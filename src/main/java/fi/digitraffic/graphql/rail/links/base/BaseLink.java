package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dataloader.BatchLoader;
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
import fi.digitraffic.graphql.rail.config.graphql.OrderByExpressionBuilder;
import fi.digitraffic.graphql.rail.config.graphql.WhereExpressionBuilder;
import fi.digitraffic.graphql.rail.entities.Train;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public abstract class BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildFieldType> {
    @Autowired
    private WhereExpressionBuilder whereExpressionBuilder;

    @Autowired
    private OrderByExpressionBuilder orderByExpressionBuilder;

    public abstract String getTypeName();

    public abstract String getFieldName();

    public abstract KeyType createKeyFromParent(ParentTOType parent);

    public abstract KeyType createKeyFromChild(ChildTOType child);

    public abstract ChildTOType createChildTOFromTuple(Tuple tuple);

    public abstract BatchLoader<KeyType, ChildFieldType> createLoader();

    public abstract Class getEntityClass();

    public abstract Expression[] getFields();

    public abstract EntityPath getEntityTable();

    public abstract BooleanExpression createWhere(List<KeyType> keys);

    public OrderSpecifier createDefaultOrder() {
        return null;
    }

    protected DataFetchingEnvironment dataFetchingEnvironment;

    @PersistenceContext
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void setup() {
        queryFactory = new JPAQueryFactory(entityManager);
    }

    public DataFetcher<CompletableFuture<ChildFieldType>> createFetcher() {
        return dataFetchingEnvironment -> {
            this.dataFetchingEnvironment = dataFetchingEnvironment;

            ParentTOType parent = dataFetchingEnvironment.getSource();

            DataLoaderRegistry dataLoaderRegistry = dataFetchingEnvironment.getContext();
            DataLoader<KeyType, ChildFieldType> dataloader = dataLoaderRegistry.getDataLoader(getTypeName() + "." + getFieldName());

            return dataloader.load(createKeyFromParent(parent));
        };
    }

    protected <ResultType> BatchLoader<KeyType, ResultType> createDataLoader(Function<List<ChildTOType>, Map<KeyType, ResultType>> childGroupFunction) {
        return parentIds -> CompletableFuture.supplyAsync(() -> {
                    List<List<KeyType>> partitions = Lists.partition(parentIds, 2499);
                    List<ChildTOType> children = new ArrayList<>(parentIds.size());

                    Class entityClass = getEntityClass();
                    PathBuilder<Train> pathBuilder = new PathBuilder<>(entityClass, entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1));

                    for (List<KeyType> partition : partitions) {
                        JPAQuery<Tuple> queryAfterFrom = queryFactory.select(getFields()).from(getEntityTable());
                        BooleanExpression basicWhere = this.createWhere(partition);
                        JPAQuery<Tuple> queryAfterWhere = createWhereQuery(queryAfterFrom, pathBuilder, basicWhere, dataFetchingEnvironment.getArgument("where"));
                        JPAQuery<Tuple> queryAfterOrderBy = createOrderByQuery(queryAfterWhere, pathBuilder, dataFetchingEnvironment.getArgument("orderBy"));

                        children.addAll(queryAfterOrderBy.fetch().stream().map(s -> this.createChildTOFromTuple(s)).collect(Collectors.toList()));
                    }

                    Map<KeyType, ResultType> childrenGroupedBy = childGroupFunction.apply(children);

                    return parentIds.stream().map(s -> childrenGroupedBy.get(s)).collect(Collectors.toList());
                }
        );
    }

    private JPAQuery<Tuple> createWhereQuery(JPAQuery<Tuple> query, PathBuilder root, BooleanExpression basicWhere, Map<String, Object> whereAsMap) {
        if (whereAsMap != null) {
            BooleanExpression whereExpression = whereExpressionBuilder.create(null, root, whereAsMap);
            return query.where(basicWhere.and(whereExpression));
        } else {
            return query.where(basicWhere);
        }
    }

    private JPAQuery<Tuple> createOrderByQuery(JPAQuery<Tuple> query, PathBuilder root, Map<String, Object> orderByAsMap) {
        if (orderByAsMap != null) {
            return query.orderBy(this.orderByExpressionBuilder.create(root, orderByAsMap));
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
