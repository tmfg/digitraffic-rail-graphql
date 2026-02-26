package fi.digitraffic.graphql.rail.queries.criteria;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
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

/**
 * Criteria API implementation of PassengerInformationMessagesQuery.
 *
 * Pros:
 * - Type-safe at compile time (when using metamodel)
 * - Refactoring-friendly
 * - IDE auto-completion support
 *
 * Cons:
 * - Very verbose code
 * - Harder to read and understand the actual query
 * - Complex queries become deeply nested
 * - More lines of code for the same functionality
 */
@Component
public class PassengerInformationMessagesQueryCriteria {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${digitraffic.max-returned-rows}")
    private Integer maxResults;

    @Autowired
    private CriteriaWhereBuilder whereBuilder;

    public String getQueryName() {
        return "passengerInformationMessagesCriteria";
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

        // Build the main predicate
        final List<Predicate> predicates = new ArrayList<>();

        // Correlated subquery: get max version for each message ID
        // JPA Criteria API doesn't directly support tuple IN subquery, so we use a correlated subquery approach
        final Subquery<Integer> maxVersionForIdSubquery = cq.subquery(Integer.class);
        final Root<PassengerInformationMessage> maxVersionRoot = maxVersionForIdSubquery.from(PassengerInformationMessage.class);
        maxVersionForIdSubquery.select(cb.max(maxVersionRoot.get("id").get("version")));
        maxVersionForIdSubquery.where(cb.equal(maxVersionRoot.get("id").get("id"), root.get("id").get("id")));

        predicates.add(cb.equal(root.get("id").get("version"), maxVersionForIdSubquery));

        // Validity conditions
        predicates.add(cb.isNull(root.get("deleted")));
        predicates.add(cb.lessThan(root.get("startValidity"), now));
        predicates.add(cb.greaterThan(root.get("endValidity"), now));

        // Dynamic where clause - use existing CriteriaWhereBuilder which returns Specification
        if (whereArgument != null && !whereArgument.isEmpty()) {
            final Specification<PassengerInformationMessage> spec = whereBuilder.build(whereArgument);
            final Predicate dynamicPredicate = spec.toPredicate(root, cq, cb);
            if (dynamicPredicate != null) {
                predicates.add(dynamicPredicate);
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.distinct(true);

        // Order by
        if (orderByArgument != null && !orderByArgument.isEmpty()) {
            final List<Order> orders = new ArrayList<>();
            for (final Map<String, Object> orderBy : orderByArgument) {
                for (final Map.Entry<String, Object> entry : orderBy.entrySet()) {
                    final String field = entry.getKey();
                    final String direction = (String) entry.getValue();
                    final Path<?> path = navigateToPath(root, field);
                    if ("DESC".equalsIgnoreCase(direction)) {
                        orders.add(cb.desc(path));
                    } else {
                        orders.add(cb.asc(path));
                    }
                }
            }
            cq.orderBy(orders);
        } else {
            cq.orderBy(cb.asc(root.get("creationDateTime")));
        }

        final TypedQuery<PassengerInformationMessage> query = entityManager.createQuery(cq);

        // Pagination
        if (skip != null) {
            query.setFirstResult(skip);
        }
        final int limit = take != null ? Math.min(take, maxResults) : maxResults;
        query.setMaxResults(limit);

        return query.getResultList().stream()
                .map(this::convertToTO)
                .toList();
    }

    /**
     * Navigate to a nested path (e.g., "id.version" -> root.get("id").get("version"))
     */
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
                null, // train - populated by link
                null, // messageStations - populated by link
                null, // audio - populated by link
                null  // video - populated by link
        );
    }
}

