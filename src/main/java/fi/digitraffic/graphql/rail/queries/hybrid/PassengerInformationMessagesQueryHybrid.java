package fi.digitraffic.graphql.rail.queries.hybrid;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.querydsl.CriteriaWhereBuilder;
import graphql.execution.AbortExecutionException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

/**
 * Hybrid implementation combining Criteria API with reusable where builder.
 *
 * Strategy:
 * - Uses Criteria API for the entire query (provides type safety for dynamic parts)
 * - Extracts the base query structure into a reusable method
 * - Applies dynamic where clause using CriteriaWhereBuilder (returns Specification)
 *
 * Pros:
 * - Cleaner than pure Criteria API due to method extraction
 * - Type-safe where clause construction via existing CriteriaWhereBuilder
 * - Reusable base query logic
 *
 * Cons:
 * - Still verbose compared to JPQL
 * - Two mental models to understand
 */
@Component
public class PassengerInformationMessagesQueryHybrid {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${digitraffic.max-returned-rows}")
    private Integer maxResults;

    @Autowired
    private CriteriaWhereBuilder whereBuilder;

    public String getQueryName() {
        return "passengerInformationMessagesHybrid";
    }

    public DataFetcher<List<PassengerInformationMessageTO>> createFetcher() {
        return dataFetchingEnvironment -> {
            try {
                return executeQuery(dataFetchingEnvironment);
            } catch (final QueryTimeoutException e) {
                throw new AbortExecutionException(e);
            }
        };
    }

    private List<PassengerInformationMessageTO> executeQuery(final DataFetchingEnvironment env) {
        final ZonedDateTime now = ZonedDateTime.now();
        final Map<String, Object> whereArgument = env.getArgument("where");
        final List<Map<String, Object>> orderByArgument = env.getArgument("orderBy");
        final Integer skip = env.getArgument("skip");
        final Integer take = env.getArgument("take");

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<PassengerInformationMessage> cq = cb.createQuery(PassengerInformationMessage.class);
        final Root<PassengerInformationMessage> root = cq.from(PassengerInformationMessage.class);

        // Apply base predicates (the fixed part of the query)
        final List<Predicate> predicates = buildBasePredicates(cb, cq, root, now);

        // Apply dynamic where clause using existing CriteriaWhereBuilder
        if (whereArgument != null && !whereArgument.isEmpty()) {
            final Specification<PassengerInformationMessage> spec = whereBuilder.build(whereArgument);
            final Predicate dynamicPredicate = spec.toPredicate(root, cq, cb);
            if (dynamicPredicate != null) {
                predicates.add(dynamicPredicate);
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.distinct(true);

        // Apply ordering
        applyOrdering(cb, cq, root, orderByArgument);

        // Execute query with pagination
        return executeWithPagination(cq, skip, take);
    }

    /**
     * Builds the base predicates that are always applied.
     * This is the "fixed" part of the query.
     */
    private List<Predicate> buildBasePredicates(
            final CriteriaBuilder cb,
            final CriteriaQuery<PassengerInformationMessage> cq,
            final Root<PassengerInformationMessage> root,
            final ZonedDateTime now) {

        final List<Predicate> predicates = new ArrayList<>();

        // Subquery: get max version for each message ID
        final Subquery<Integer> maxVersionSubquery = cq.subquery(Integer.class);
        final Root<PassengerInformationMessage> subRoot = maxVersionSubquery.from(PassengerInformationMessage.class);
        maxVersionSubquery.select(cb.max(subRoot.get("id").get("version")));
        maxVersionSubquery.where(cb.equal(subRoot.get("id").get("id"), root.get("id").get("id")));

        // Main predicates
        predicates.add(cb.equal(root.get("id").get("version"), maxVersionSubquery));
        predicates.add(cb.isNull(root.get("deleted")));
        predicates.add(cb.lessThan(root.get("startValidity"), now));
        predicates.add(cb.greaterThan(root.get("endValidity"), now));

        return predicates;
    }

    /**
     * Applies ordering to the query.
     */
    private void applyOrdering(
            final CriteriaBuilder cb,
            final CriteriaQuery<PassengerInformationMessage> cq,
            final Root<PassengerInformationMessage> root,
            final List<Map<String, Object>> orderByArgument) {

        if (orderByArgument != null && !orderByArgument.isEmpty()) {
            final List<Order> orders = new ArrayList<>();
            for (final Map<String, Object> orderBy : orderByArgument) {
                for (final Map.Entry<String, Object> entry : orderBy.entrySet()) {
                    final String field = entry.getKey();
                    final String direction = (String) entry.getValue();
                    final Path<?> path = navigateToPath(root, field);
                    orders.add("DESC".equalsIgnoreCase(direction) ? cb.desc(path) : cb.asc(path));
                }
            }
            cq.orderBy(orders);
        } else {
            cq.orderBy(cb.asc(root.get("creationDateTime")));
        }
    }

    /**
     * Executes the query with pagination.
     */
    private List<PassengerInformationMessageTO> executeWithPagination(
            final CriteriaQuery<PassengerInformationMessage> cq,
            final Integer skip,
            final Integer take) {

        final TypedQuery<PassengerInformationMessage> query = entityManager.createQuery(cq);

        if (skip != null) {
            query.setFirstResult(skip);
        }
        final int limit = take != null ? Math.min(take, maxResults) : maxResults;
        query.setMaxResults(limit);

        return query.getResultList().stream()
                .map(this::convertToTO)
                .toList();
    }

    private Path<?> navigateToPath(final Root<?> root, final String fieldPath) {
        final String[] parts = fieldPath.split("\\.");
        Path<?> path = root;
        for (final String part : parts) {
            path = path.get(part);
        }
        return path;
    }

    private PassengerInformationMessageTO convertToTO(final PassengerInformationMessage entity) {
        return new PassengerInformationMessageTO(
                entity.id.id,
                entity.id.version,
                entity.creationDateTime,
                entity.startValidity,
                entity.endValidity,
                entity.trainDepartureDate,
                entity.trainNumber != null ? entity.trainNumber.intValue() : null,
                null,
                null,
                null,
                null
        );
    }
}

