package fi.digitraffic.graphql.rail.links.base;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.TrainId;

/**
 * Builds an optimised JPQL WHERE clause for composite train-id keys.
 *
 * Rather than emitting one predicate per key, the keys are grouped by departure date
 * so that the resulting SQL can use an IN-list for the train numbers within each date:
 * <pre>
 * (e.departureDate = :dd0 AND e.trainNumber IN :tn0) OR
 * (e.departureDate = :dd1 AND e.trainNumber IN :tn1)
 * </pre>
 */
public final class TrainIdWhereClause {

    private TrainIdWhereClause() {}

    /**
     * Builds the WHERE clause for {@link TrainId} keys (Long train number, departureDate).
     */
    public static KeyWhereClause build(
            final String alias,
            final String dateField,
            final String numberField,
            final List<TrainId> keys) {

        final Map<LocalDate, Set<Long>> byDate = new HashMap<>();
        for (final TrainId key : keys) {
            byDate.computeIfAbsent(key.departureDate, d -> new HashSet<>()).add(key.trainNumber);
        }

        return buildClauses(alias, dateField, numberField, byDate);
    }

    /**
     * Builds the WHERE clause for {@link StringVirtualDepartureDateTrainId} keys
     * (String train number, virtualDepartureDate).
     */
    public static KeyWhereClause buildForVirtualDepartureDate(
            final String alias,
            final String dateField,
            final String numberField,
            final List<StringVirtualDepartureDateTrainId> keys) {

        final Map<LocalDate, Set<String>> byDate = new HashMap<>();
        for (final StringVirtualDepartureDateTrainId key : keys) {
            byDate.computeIfAbsent(key.virtualDepartureDate, d -> new HashSet<>()).add(key.trainNumber);
        }

        return buildClauses(alias, dateField, numberField, byDate);
    }

    private static <T> KeyWhereClause buildClauses(
            final String alias,
            final String dateField,
            final String numberField,
            final Map<LocalDate, Set<T>> byDate) {

        final Map<String, Object> params = new HashMap<>();
        final List<String> clauses = new ArrayList<>();

        int idx = 0;
        for (final Map.Entry<LocalDate, Set<T>> entry : byDate.entrySet()) {
            final String dateParam = "dd" + idx;
            final String numberParam = "tn" + idx;
            idx++;

            params.put(dateParam, entry.getKey());
            params.put(numberParam, List.copyOf(entry.getValue()));

            clauses.add(String.format(
                    "(%s.%s = :%s AND %s.%s IN :%s)",
                    alias, dateField, dateParam,
                    alias, numberField, numberParam));
        }

        return new KeyWhereClause(String.join(" OR ", clauses), params);
    }
}
