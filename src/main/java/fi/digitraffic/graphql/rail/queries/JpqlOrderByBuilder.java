package fi.digitraffic.graphql.rail.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import graphql.execution.AbortExecutionException;

/**
 * Builds JPQL ORDER BY clause strings from a list of order specifications.
 *
 * Handles nested structures like:
 * - Simple: [{"trainNumber": "ASCENDING"}] -> "alias.trainNumber ASC"
 * - Nested: [{"trainType": {"name": "ASCENDING"}}] -> "alias.trainType.name ASC"
 */
@Service
public class JpqlOrderByBuilder {

    /**
     * Builds a JPQL ORDER BY clause fragment.
     *
     * @param alias The entity alias (e.g., "e" for "SELECT e FROM Train e")
     * @param orderByList List of order specifications from GraphQL
     * @return ORDER BY clause fragment (without "ORDER BY" keyword)
     */
    public String build(final String alias, final List<Map<String, Object>> orderByList) {
        if (orderByList == null || orderByList.isEmpty()) {
            return "";
        }

        final List<String> orderClauses = new ArrayList<>();

        for (final Map<String, Object> orderByMap : orderByList) {
            if (orderByMap == null || orderByMap.isEmpty()) {
                continue;
            }
            final Pair<List<String>, String> pathAndDirection = extractPathAndDirection(orderByMap, new ArrayList<>());
            final String path = alias + "." + String.join(".", pathAndDirection.getFirst());
            final String direction = mapDirection(pathAndDirection.getSecond());
            orderClauses.add(path + " " + direction);
        }

        return String.join(", ", orderClauses);
    }

    /**
     * Recursively extracts the field path and direction from nested map structure.
     * Example: {"trainType": {"name": "ASCENDING"}} -> (["trainType", "name"], "ASCENDING")
     */
    @SuppressWarnings("unchecked")
    private Pair<List<String>, String> extractPathAndDirection(final Map<String, Object> map, final List<String> paths) {
        if (map == null || map.isEmpty()) {
            throw new AbortExecutionException("orderBy entry must not be empty");
        }

        final Map.Entry<String, Object> entry = map.entrySet().iterator().next();
        final String key = entry.getKey();
        final Object value = entry.getValue();

        JpqlSafeIdentifier.validate(key);
        paths.add(key);

        if (value instanceof final Map<?, ?> nestedMap) {
            return extractPathAndDirection((Map<String, Object>) nestedMap, paths);
        } else if (value instanceof final String direction) {
            return Pair.of(paths, direction);
        } else {
            throw new AbortExecutionException("orderBy direction must be a string (ASCENDING or DESCENDING), got: " +
                    (value == null ? "null" : value.getClass().getSimpleName()));
        }
    }

    /**
     * Maps GraphQL direction enum (ASCENDING/DESCENDING) to JPQL (ASC/DESC).
     * Throws {@link AbortExecutionException} for unknown values.
     */
    private String mapDirection(final String graphqlDirection) {
        return switch (graphqlDirection) {
            case "ASCENDING" -> "ASC";
            case "DESCENDING" -> "DESC";
            default -> throw new AbortExecutionException(
                    "orderBy direction must be ASCENDING or DESCENDING, got: " + graphqlDirection);
        };
    }
}



