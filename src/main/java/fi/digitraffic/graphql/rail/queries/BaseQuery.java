package fi.digitraffic.graphql.rail.queries;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.querydsl.OrderByExpressionBuilder;
import fi.digitraffic.graphql.rail.querydsl.WhereExpressionBuilder;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public abstract class BaseQuery<T> {
    @Autowired
    private WhereExpressionBuilder whereExpressionBuilder;

    @Autowired
    private OrderByExpressionBuilder orderByExpressionBuilder;

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

            List<Tuple> rows = queryAfterLimit.fetch();

            return rows.stream().map(s -> convertEntityToTO(s)).collect(Collectors.toList());
        };
    }

    public OrderSpecifier createDefaultOrder() {
        return null;
    }

    protected JPAQuery<Tuple> createLimitQuery(JPAQuery<Tuple> query, Object limitArgument) {
        if (limitArgument != null) {
            long limit = Math.min(MAX_RESULTS, (Integer) limitArgument);
            return query.limit(limit);
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
            Map<String, Object> properWhereMap = this.replaceOffsetsWithZonedDateTimes(whereAsMap);

            BooleanExpression whereExpression = whereExpressionBuilder.create(null, root, properWhereMap);
            return query.where(basicWhere.and(whereExpression));
        } else {
            return query.where(basicWhere);
        }
    }

    private Map<String, Object> replaceOffsetsWithZonedDateTimes(Map<String, Object> whereAsMap) {
        for (String key : whereAsMap.keySet()) {
            Object value = whereAsMap.get(key);
            if (value instanceof Map) {
                this.replaceOffsetsWithZonedDateTimes((Map<String, Object>) value);
            } else if (value instanceof OffsetDateTime) {
                whereAsMap.put(key, ((OffsetDateTime) value).toZonedDateTime());
            }
        }

        return whereAsMap;
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
