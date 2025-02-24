package fi.digitraffic.graphql.rail.queries;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import fi.digitraffic.graphql.rail.querydsl.OrderByExpressionBuilder;
import fi.digitraffic.graphql.rail.querydsl.WhereExpressionBuilder;
import graphql.execution.AbortExecutionException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.QueryTimeoutException;

public abstract class BaseQuery<T> {
    @Autowired
    private WhereExpressionBuilder whereExpressionBuilder;

    @Autowired
    private OrderByExpressionBuilder orderByExpressionBuilder;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${digitraffic.max-returned-rows}")
    public Integer MAX_RESULTS;

    protected JPAQueryFactory queryFactory;

    @PostConstruct
    public void setup() {
        queryFactory = new JPAQueryFactory(entityManager);
    }

    public abstract String getQueryName();

    public abstract Class<T> getEntityClass();

    public abstract Expression<?>[] getFields();

    public abstract EntityPath<T> getEntityTable();

    public abstract BooleanExpression createWhereFromArguments(DataFetchingEnvironment dataFetchingEnvironment);

    public abstract T convertEntityToTO(Tuple tuple);

    public DataFetcher<List<T>> createFetcher() {
        return dataFetchingEnvironment -> {
            final Class<T> entityClass = getEntityClass();
            final PathBuilder<T> pathBuilder = new PathBuilder<>(entityClass,
                    entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1));

            final BooleanExpression basicWhere = createWhereFromArguments(dataFetchingEnvironment);

            final JPAQuery<Tuple> queryAfterFrom = queryFactory.select(
                            getFields())
                    .from(getEntityTable());

            final JPAQuery<Tuple> queryAfterWhere =
                    createWhereQuery(queryAfterFrom, pathBuilder, basicWhere, dataFetchingEnvironment.getArgument("where"));
            final JPAQuery<Tuple> queryAfterOrderBy =
                    createOrderByQuery(queryAfterWhere, pathBuilder, dataFetchingEnvironment.getArgument("orderBy"));
            final JPAQuery<Tuple> queryAfterOffset = createOffsetQuery(queryAfterOrderBy, dataFetchingEnvironment.getArgument("skip"));
            final JPAQuery<Tuple> queryAfterLimit = createLimitQuery(queryAfterOffset, dataFetchingEnvironment.getArgument("take"));

            try {
                final List<Tuple> rows = queryAfterLimit.fetch();
                return rows.stream().map(this::convertEntityToTO).toList();
            } catch (final QueryTimeoutException e) {
                throw new AbortExecutionException(e);
            } catch (final IllegalArgumentException e) {
                throw new AbortExecutionException(e.getMessage());
            }
        };
    }

    public OrderSpecifier createDefaultOrder() {
        return null;
    }

    protected JPAQuery<Tuple> createLimitQuery(final JPAQuery<Tuple> query, final Object limitArgument) {
        if (limitArgument != null) {
            final long limit = Math.min(MAX_RESULTS, (Integer) limitArgument);
            return query.limit(limit);
        } else {
            return query.limit(MAX_RESULTS);
        }
    }

    protected JPAQuery<Tuple> createOffsetQuery(final JPAQuery<Tuple> query, final Object skipArgument) {
        if (skipArgument != null) {
            return query.offset(((Integer) skipArgument).longValue());
        } else {
            return query;
        }
    }

    protected JPAQuery<Tuple> createWhereQuery(final JPAQuery<Tuple> query, final PathBuilder root, final BooleanExpression basicWhere,
                                               final Map<String, Object> whereAsMap) {
        if (whereAsMap != null) {
            final Map<String, Object> properWhereMap = this.replaceOffsetsWithZonedDateTimes(whereAsMap);

            final BooleanExpression whereExpression = whereExpressionBuilder.create(null, root, properWhereMap);
            return query.where(basicWhere.and(whereExpression));
        } else {
            return query.where(basicWhere);
        }
    }

    private Map<String, Object> replaceOffsetsWithZonedDateTimes(final Map<String, Object> whereAsMap) {
        for (final String key : whereAsMap.keySet()) {
            final Object value = whereAsMap.get(key);
            if (value instanceof Map) {
                this.replaceOffsetsWithZonedDateTimes((Map<String, Object>) value);
            } else if (value instanceof OffsetDateTime) {
                whereAsMap.put(key, ((OffsetDateTime) value).toZonedDateTime());
            }
        }

        return whereAsMap;
    }

    protected JPAQuery<Tuple> createOrderByQuery(JPAQuery<Tuple> query, final PathBuilder root, final List<Map<String, Object>> orderByArgument) {
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
