package fi.digitraffic.graphql.rail.links.base;

import java.util.Map;

/**
 * Holds a JPQL WHERE clause fragment together with its named parameters.
 */
public record KeyWhereClause(String jpql, Map<String, Object> params) {
    public static final KeyWhereClause EMPTY = new KeyWhereClause("", Map.of());
}
