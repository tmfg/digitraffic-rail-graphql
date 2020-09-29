package fi.digitraffic.graphql.rail.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.digitraffic.graphql.rail.config.graphql.WhereExpressionBuilder;
import fi.digitraffic.graphql.rail.entities.Train;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public abstract class BaseQuery<T> {
    @Autowired
    private WhereExpressionBuilder whereExpressionBuilder;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${digitraffic.max-returned-rows}")
    public Integer MAX_RESULTS;

    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void setup() {
        queryFactory = new JPAQueryFactory(entityManager);
    }

    public abstract String getQueryName();

    public abstract Class getEntityClass();

    public abstract Expression[] getFields();

    public abstract EntityPath getEntityTable();

    public abstract BooleanExpression createWhereFromArguments(DataFetchingEnvironment dataFetchingEnvironment);

    public abstract T convertEntityToTO(Tuple tuple);

    public DataFetcher<List<T>> createFetcher() {
        return dataFetchingEnvironment -> {
            Class entityClass = getEntityClass();
            PathBuilder<Train> pathBuilder = new PathBuilder<>(entityClass, entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1));

            JPAQuery<Tuple> queryAfterFrom = queryFactory.select(
                    getFields())
                    .from(getEntityTable());

            BooleanExpression basicWhere = createWhereFromArguments(dataFetchingEnvironment);

            JPAQuery<Tuple> queryAfterWhere = createWhereQuery(queryAfterFrom, pathBuilder, basicWhere, dataFetchingEnvironment.getArgument("where"));
            JPAQuery<Tuple> queryAfterOrderBy = createOrderByQuery(queryAfterWhere, pathBuilder, dataFetchingEnvironment.getArgument("orderBy"));
            JPAQuery<Tuple> queryAfterOffset = createOffsetQuery(queryAfterOrderBy, dataFetchingEnvironment.getArgument("skip"));
            JPAQuery<Tuple> queryAfterLimit = createLimitQuery(queryAfterOffset, dataFetchingEnvironment.getArgument("take"));

            List<Tuple> trains = queryAfterLimit.fetch();

            return trains.stream().map(s -> convertEntityToTO(s)).collect(Collectors.toList());
        };
    }

    public OrderSpecifier createDefaultOrder() {
        return null;
    }

    private JPAQuery<Tuple> createLimitQuery(JPAQuery<Tuple> query, Object limitArgument) {
        if (limitArgument != null) {
            return query.limit(((Integer) limitArgument).longValue());
        } else {
            return query.limit(MAX_RESULTS);
        }
    }

    private JPAQuery<Tuple> createOffsetQuery(JPAQuery<Tuple> query, Object skipArgument) {
        if (skipArgument != null) {
            return query.offset(((Integer) skipArgument).longValue());
        } else {
            return query;
        }
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
            Pair<List<String>, Object> order = this.getPathAndDeepValueAsString(orderByAsMap, new ArrayList<>());
            Path<Object> dynamicOrder = getProperty(root, order.getLeft());
            Order orderAsDSL = order.getRight().equals("ASCENDING") ? Order.ASC : Order.DESC;
            return query.orderBy(new OrderSpecifier(orderAsDSL, dynamicOrder));
        } else {
            OrderSpecifier defaultOrder = this.createDefaultOrder();
            if (defaultOrder != null) {
                return query.orderBy(defaultOrder);
            } else {
                return query;
            }
        }
    }

    private Path<Object> getProperty(PathBuilder root, List<String> paths) {
        PathBuilder prop = root;
        for (String path : paths) {
            prop = prop.get(path);
        }

        return prop;
    }

    private Pair<List<String>, Object> getPathAndDeepValueAsString(Map rootValue, List<String> paths) {
        Set<Map.Entry> entries = rootValue.entrySet();
        Map.Entry entry = entries.iterator().next();
        Object value = entry.getValue();
        paths.add((String) entry.getKey());
        if (!entries.isEmpty() && value instanceof Map) {
            return getPathAndDeepValueAsString((Map) value, paths);
        } else {
            return Pair.of(paths, value);
        }
    }
}
