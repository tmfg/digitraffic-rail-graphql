package fi.digitraffic.graphql.rail.queries.criteria;

import java.time.ZonedDateTime;
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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

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
        return env -> {
            try {
                return executeQuery(env);
            } catch (final QueryTimeoutException e) {
                throw new AbortExecutionException(e);
            }
        };
    }

    private List<PassengerInformationMessageTO> executeQuery(final DataFetchingEnvironment env) {
        final var cb = entityManager.getCriteriaBuilder();
        final var cq = cb.createQuery(PassengerInformationMessage.class);
        final var root = cq.from(PassengerInformationMessage.class);
        final var now = ZonedDateTime.now();

        cq.where(cb.and(
                maxVersionPredicate(cq, cb, root),
                cb.isNull(root.get("deleted")),
                cb.lessThan(root.get("startValidity"), now),
                cb.greaterThan(root.get("endValidity"), now),
                dynamicWhere(env.getArgument("where"), root, cq, cb)
        ));
        cq.distinct(true);
        applyOrderBy(env.getArgument("orderBy"), root, cq, cb);

        final var query = entityManager.createQuery(cq);
        final Integer skip = env.getArgument("skip");
        final Integer take = env.getArgument("take");
        if (skip != null) query.setFirstResult(skip);
        query.setMaxResults(take != null ? Math.min(take, maxResults) : maxResults);

        return query.getResultList().stream().map(this::convertToTO).toList();
    }

    private Predicate maxVersionPredicate(final CriteriaQuery<?> cq, final CriteriaBuilder cb,
                                          final Root<PassengerInformationMessage> root) {
        final var sub = cq.subquery(Integer.class);
        final var subRoot = sub.from(PassengerInformationMessage.class);
        sub.select(cb.max(subRoot.get("id").get("version")))
           .where(cb.equal(subRoot.get("id").get("id"), root.get("id").get("id")));
        return cb.equal(root.get("id").get("version"), sub);
    }

    private Predicate dynamicWhere(final Map<String, Object> where, final Root<PassengerInformationMessage> root,
                                   final CriteriaQuery<PassengerInformationMessage> cq, final CriteriaBuilder cb) {
        if (where == null || where.isEmpty()) return cb.conjunction();
        return whereBuilder.<PassengerInformationMessage>build(where).toPredicate(root, cq, cb);
    }

    private void applyOrderBy(final List<Map<String, Object>> orderBy, final Root<?> root,
                              final CriteriaQuery<?> cq, final CriteriaBuilder cb) {
        if (orderBy == null || orderBy.isEmpty()) {
            cq.orderBy(cb.asc(root.get("creationDateTime")));
            return;
        }
        cq.orderBy(orderBy.stream()
                .flatMap(m -> m.entrySet().stream())
                .map(e -> "DESC".equalsIgnoreCase((String) e.getValue())
                        ? cb.desc(toPath(root, e.getKey()))
                        : cb.asc(toPath(root, e.getKey())))
                .toList());
    }

    private Path<?> toPath(final Path<?> root, final String field) {
        Path<?> path = root;
        for (final String part : field.split("\\.")) path = path.get(part);
        return path;
    }

    private PassengerInformationMessageTO convertToTO(final PassengerInformationMessage e) {
        return new PassengerInformationMessageTO(
                e.id.id, e.id.version, e.creationDateTime, e.startValidity, e.endValidity,
                e.trainDepartureDate, e.trainNumber != null ? e.trainNumber.intValue() : null,
                null, null, null, null);
    }
}

