package fi.digitraffic.graphql.rail.queries.jpql;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import graphql.execution.AbortExecutionException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.TypedQuery;

/**
 * Base class for JPQL-based GraphQL queries.
 * This mirrors the functionality of BaseQuery but uses JPQL instead of QueryDSL.
 *
 * @param <E> Entity type
 * @param <T> Transfer object type
 */
public abstract class BaseQueryJpql<E, T> {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private JpqlWhereBuilder whereBuilder;

    @Autowired
    private JpqlOrderByBuilder orderByBuilder;

    @Value("${digitraffic.max-returned-rows}")
    protected Integer maxResults;

    /**
     * @return The GraphQL query name (e.g., "trains", "passengerInformationMessages")
     */
    public abstract String getQueryName();

    /**
     * @return The entity class being queried
     */
    public abstract Class<E> getEntityClass();

    /**
     * @return The JPQL entity alias (e.g., "t" for "SELECT t FROM Train t")
     */
    public String getEntityAlias() {
        return getEntityClass().getSimpleName().substring(0, 1).toLowerCase();
    }

    /**
     * Builds the base JPQL query string (SELECT ... FROM ... WHERE fixed conditions).
     * Subclasses override this to add entity-specific base conditions.
     *
     * @param alias The entity alias
     * @param parameters List to collect parameter values
     * @param paramCounter Counter for positional parameters
     * @return The base JPQL query string
     */
    public String buildBaseQuery(final String alias, final List<Object> parameters, final AtomicInteger paramCounter) {
        return "SELECT " + alias + " FROM " + getEntityClass().getSimpleName() + " " + alias;
    }

    /**
     * Builds the base WHERE clause conditions that are always applied.
     * Returns empty string if no base conditions.
     *
     * @param alias The entity alias
     * @param env The GraphQL data fetching environment
     * @param parameters List to collect parameter values
     * @param paramCounter Counter for positional parameters
     * @return WHERE clause fragment (without "WHERE" keyword), or empty string
     */
    public abstract String buildBaseWhereClause(String alias, DataFetchingEnvironment env,
                                                 List<Object> parameters, AtomicInteger paramCounter);

    /**
     * Converts an entity to its transfer object.
     */
    public abstract T convertEntityToTO(E entity);

    /**
     * @return Default ORDER BY clause (e.g., "t.id ASC"), or null for no default ordering
     */
    public String getDefaultOrderBy(final String alias) {
        return null;
    }

    /**
     * Creates the DataFetcher for this query.
     */
    public DataFetcher<List<T>> createFetcher() {
        return env -> {
            try {
                return executeQuery(env);
            } catch (final QueryTimeoutException e) {
                throw new AbortExecutionException(e);
            } catch (final IllegalArgumentException e) {
                throw new AbortExecutionException(e.getMessage());
            }
        };
    }

    /**
     * Executes the query and returns results.
     */
    protected List<T> executeQuery(final DataFetchingEnvironment env) {
        final String alias = getEntityAlias();
        final List<Object> parameters = new ArrayList<>();
        final AtomicInteger paramCounter = new AtomicInteger(1);

        // Build the query
        final StringBuilder jpql = new StringBuilder();
        jpql.append(buildBaseQuery(alias, parameters, paramCounter));

        // Build WHERE clause
        final String whereClause = buildWhereClause(alias, env, parameters, paramCounter);
        if (!whereClause.isEmpty()) {
            jpql.append(" WHERE ").append(whereClause);
        }

        // Build ORDER BY clause
        final String orderByClause = buildOrderByClause(alias, env);
        if (!orderByClause.isEmpty()) {
            jpql.append(" ORDER BY ").append(orderByClause);
        }

        // Create and configure query
        final TypedQuery<E> query = entityManager.createQuery(jpql.toString(), getEntityClass());

        // Set parameters
        for (int i = 0; i < parameters.size(); i++) {
            query.setParameter(i + 1, parameters.get(i));
        }

        // Set pagination
        final Integer skip = env.getArgument("skip");
        if (skip != null) {
            query.setFirstResult(skip);
        }

        final Integer take = env.getArgument("take");
        final int limit = take != null ? Math.min(take, maxResults) : maxResults;
        query.setMaxResults(limit);

        // Execute and convert
        return query.getResultList().stream()
                .map(this::convertEntityToTO)
                .toList();
    }

    /**
     * Builds the complete WHERE clause combining base conditions and dynamic where argument.
     */
    protected String buildWhereClause(final String alias, final DataFetchingEnvironment env,
                                      final List<Object> parameters, final AtomicInteger paramCounter) {
        final List<String> conditions = new ArrayList<>();

        // Add base where conditions
        final String baseWhere = buildBaseWhereClause(alias, env, parameters, paramCounter);
        if (!baseWhere.isEmpty()) {
            conditions.add(baseWhere);
        }

        // Add dynamic where conditions from GraphQL argument
        final Map<String, Object> whereArgument = env.getArgument("where");
        if (whereArgument != null && !whereArgument.isEmpty()) {
            final Map<String, Object> convertedWhere = replaceOffsetsWithZonedDateTimes(whereArgument);
            final String dynamicWhere = whereBuilder.build(alias, convertedWhere, parameters, paramCounter);
            if (!dynamicWhere.isEmpty()) {
                conditions.add(dynamicWhere);
            }
        }

        return String.join(" AND ", conditions);
    }

    /**
     * Builds the ORDER BY clause.
     */
    protected String buildOrderByClause(final String alias, final DataFetchingEnvironment env) {
        final List<Map<String, Object>> orderByArgument = env.getArgument("orderBy");

        if (orderByArgument != null && !orderByArgument.isEmpty()) {
            return orderByBuilder.build(alias, orderByArgument);
        }

        final String defaultOrder = getDefaultOrderBy(alias);
        return defaultOrder != null ? defaultOrder : "";
    }

    /**
     * Converts OffsetDateTime values to ZonedDateTime in the where map.
     */
    protected static Map<String, Object> replaceOffsetsWithZonedDateTimes(final Map<String, Object> whereAsMap) {
        for (final String key : whereAsMap.keySet()) {
            final Object value = whereAsMap.get(key);
            if (value instanceof Map) {
                replaceOffsetsWithZonedDateTimes((Map<String, Object>) value);
            } else if (value instanceof OffsetDateTime) {
                whereAsMap.put(key, ((OffsetDateTime) value).toZonedDateTime());
            }
        }
        return whereAsMap;
    }
}

