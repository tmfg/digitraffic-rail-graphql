package fi.digitraffic.graphql.rail.querydsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

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
     * @param alias The entity alias (e.g., "t" for "SELECT t FROM Train t")
     * @param orderByList List of order specifications from GraphQL
     * @return ORDER BY clause fragment (without "ORDER BY" keyword)
     */
    public String build(final String alias, final List<Map<String, Object>> orderByList) {
        if (orderByList == null || orderByList.isEmpty()) {
            return "";
        }

        final List<String> orderClauses = new ArrayList<>();

        for (final Map<String, Object> orderByMap : orderByList) {
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
        final Map.Entry<String, Object> entry = map.entrySet().iterator().next();
        final String key = entry.getKey();
        final Object value = entry.getValue();

        JpqlSafeIdentifier.validate(key);
        paths.add(key);

        if (value instanceof final Map<?, ?> nestedMap) {
            return extractPathAndDirection((Map<String, Object>) nestedMap, paths);
        } else {
            return Pair.of(paths, (String) value);
        }
    }

    /**
     * Maps GraphQL direction enum (ASCENDING/DESCENDING) to JPQL (ASC/DESC).
     */
    private String mapDirection(final String graphqlDirection) {
        return "ASCENDING".equals(graphqlDirection) ? "ASC" : "DESC";
    }
}

