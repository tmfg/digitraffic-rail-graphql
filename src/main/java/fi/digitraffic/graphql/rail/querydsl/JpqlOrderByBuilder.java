package fi.digitraffic.graphql.rail.querydsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Builds JPQL ORDER BY clause strings from a list of order specifications.
 *
 * Example input: [{"trainNumber": "ASC"}, {"departureDate": "DESC"}]
 * Example output: "t.trainNumber ASC, t.departureDate DESC"
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

        for (final Map<String, Object> orderBy : orderByList) {
            for (final Map.Entry<String, Object> entry : orderBy.entrySet()) {
                final String field = entry.getKey();
                final String direction = (String) entry.getValue();
                final String path = buildPath(alias, field);
                orderClauses.add(path + " " + direction);
            }
        }

        return String.join(", ", orderClauses);
    }

    /**
     * Builds a dot-notation path for nested fields.
     * Handles cases like "id.version" -> "t.id.version"
     */
    private String buildPath(final String alias, final String field) {
        // Field may already contain dots for nested paths
        return alias + "." + field;
    }
}

