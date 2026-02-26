package fi.digitraffic.graphql.rail.queries.jpql;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

/**
 * JPQL implementation of PassengerInformationMessagesQuery.
 * Extends BaseQueryJpql to provide a minimal testable JPQL-based solution.
 */
@Component
public class PassengerInformationMessagesQueryJpql extends BaseQueryJpql<PassengerInformationMessage, PassengerInformationMessageTO> {

    @Override
    public String getQueryName() {
        return "passengerInformationMessagesJpql";
    }

    @Override
    public Class<PassengerInformationMessage> getEntityClass() {
        return PassengerInformationMessage.class;
    }

    @Override
    public String getEntityAlias() {
        return "m";
    }

    @Override
    public String buildBaseQuery(final String alias, final List<Object> parameters, final AtomicInteger paramCounter) {
        // This query includes the subquery for max versions
        return """
            SELECT DISTINCT %s FROM PassengerInformationMessage %s
            WHERE (%s.id.id, %s.id.version) IN (
                SELECT m2.id.id, MAX(m2.id.version)
                FROM PassengerInformationMessage m2
                GROUP BY m2.id.id
            )""".formatted(alias, alias, alias, alias);
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                        final List<Object> parameters, final AtomicInteger paramCounter) {
        final ZonedDateTime now = ZonedDateTime.now();

        // deleted IS NULL AND startValidity < now AND endValidity > now
        final int p1 = paramCounter.getAndIncrement();
        final int p2 = paramCounter.getAndIncrement();
        parameters.add(now);
        parameters.add(now);

        return "%s.deleted IS NULL AND %s.startValidity < ?%d AND %s.endValidity > ?%d"
                .formatted(alias, alias, p1, alias, p2);
    }

    @Override
    public String getDefaultOrderBy(final String alias) {
        return alias + ".creationDateTime ASC";
    }

    @Override
    public PassengerInformationMessageTO convertEntityToTO(final PassengerInformationMessage entity) {
        return new PassengerInformationMessageTO(
                entity.id.id,
                entity.id.version,
                entity.creationDateTime,
                entity.startValidity,
                entity.endValidity,
                entity.trainDepartureDate,
                entity.trainNumber != null ? entity.trainNumber.intValue() : null,
                null, // train - populated by link
                null, // messageStations - populated by link
                null, // audio - populated by link
                null  // video - populated by link
        );
    }

    @Override
    public DataFetcher<List<PassengerInformationMessageTO>> createFetcher() {
        // Override to handle the special case where buildBaseQuery already includes WHERE
        return env -> {
            try {
                return executeQueryWithExistingWhere(env);
            } catch (final Exception e) {
                throw new graphql.execution.AbortExecutionException(e);
            }
        };
    }

    /**
     * Special execution method for queries where buildBaseQuery already contains WHERE clause.
     */
    private List<PassengerInformationMessageTO> executeQueryWithExistingWhere(final DataFetchingEnvironment env) {
        final String alias = getEntityAlias();
        final java.util.List<Object> parameters = new java.util.ArrayList<>();
        final AtomicInteger paramCounter = new AtomicInteger(1);

        // Build the query (already contains WHERE for the subquery condition)
        final StringBuilder jpql = new StringBuilder();
        jpql.append(buildBaseQuery(alias, parameters, paramCounter));

        // Add base where conditions with AND (since WHERE already exists)
        final String baseWhere = buildBaseWhereClause(alias, env, parameters, paramCounter);
        if (!baseWhere.isEmpty()) {
            jpql.append(" AND ").append(baseWhere);
        }

        // Add dynamic where conditions
        final Map<String, Object> whereArgument = env.getArgument("where");
        if (whereArgument != null && !whereArgument.isEmpty()) {
            final var convertedWhere = replaceOffsetsWithZonedDateTimes(whereArgument);
            final var whereBuilder = new fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder();
            final String dynamicWhere = whereBuilder.build(alias, convertedWhere, parameters, paramCounter);
            if (!dynamicWhere.isEmpty()) {
                jpql.append(" AND ").append(dynamicWhere);
            }
        }

        // Build ORDER BY clause
        final java.util.List<Map<String, Object>> orderByArgument = env.getArgument("orderBy");
        if (orderByArgument != null && !orderByArgument.isEmpty()) {
            final var orderByBuilder = new fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder();
            jpql.append(" ORDER BY ").append(orderByBuilder.build(alias, orderByArgument));
        } else {
            final String defaultOrder = getDefaultOrderBy(alias);
            if (defaultOrder != null && !defaultOrder.isEmpty()) {
                jpql.append(" ORDER BY ").append(defaultOrder);
            }
        }

        // Create and configure query
        final jakarta.persistence.TypedQuery<PassengerInformationMessage> query =
                entityManager.createQuery(jpql.toString(), getEntityClass());

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
}
