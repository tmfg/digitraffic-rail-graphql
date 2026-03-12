package fi.digitraffic.graphql.rail.links.base.jpql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.digitraffic.graphql.rail.entities.TrainId;

/**
 * Builds an optimised JPQL WHERE clause for a list of composite {@link TrainId} keys.
 *
 * <p>Rather than emitting one predicate per key, the keys are grouped by departure date
 * so that the resulting SQL can use an IN-list for the train numbers within each date:
 * <pre>
 * (e.departureDate = :dd0 AND e.trainNumber IN :tn0) OR
 * (e.departureDate = :dd1 AND e.trainNumber IN :tn1)
 * </pre>
 *
 *
 */
public final class TrainIdJpqlWhereClause {

    private TrainIdJpqlWhereClause() {}

    /**
     * Builds the WHERE clause and its named parameters.
     *
     * @param alias      the JPQL entity alias (e.g. {@code "e"})
     * @param dateField  the field path for departure date relative to the alias (e.g. {@code "departureDate"} or {@code "id.departureDate"})
     * @param numberField the field path for train number relative to the alias (e.g. {@code "trainNumber"} or {@code "id.trainNumber"})
     * @param keys       the list of {@link TrainId} values to match
     * @return a record containing the JPQL fragment and named parameter map
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

        final Map<String, Object> params = new HashMap<>();
        final List<String> clauses = new ArrayList<>();

        int idx = 0;
        for (final Map.Entry<LocalDate, Set<Long>> entry : byDate.entrySet()) {
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

        final String jpql = String.join(" OR ", clauses);
        return new KeyWhereClause(jpql, params);
    }
}
