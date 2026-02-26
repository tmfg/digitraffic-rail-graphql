package fi.digitraffic.graphql.rail.querydsl;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import graphql.execution.AbortExecutionException;

/**
 * Builds JPQL WHERE clause strings from a nested Map structure (GraphQL input).
 *
 * This mirrors the functionality of WhereExpressionBuilder but for JPQL strings.
 *
 * Example input: {"trainNumber": {"equals": 123}}
 * Example output: "e.trainNumber = :p0" with params {p0: 123}
 */
@Service
public class JpqlWhereBuilder {

    /**
     * Result record containing the JPQL fragment and named parameters.
     */
    public record JpqlResult(String jpql, Map<String, Object> params) {}

    private final EnumConverter enumConverter;

    public JpqlWhereBuilder(final EnumConverter enumConverter) {
        this.enumConverter = enumConverter;
    }

    public JpqlWhereBuilder() {
        this.enumConverter = null;
    }

    /**
     * Builds a JPQL WHERE clause fragment with named parameters.
     * Preferred API for cleaner parameter handling.
     *
     * @param alias The entity alias (e.g., "e" for "SELECT e FROM Entity e")
     * @param where The where condition map from GraphQL
     * @return JpqlResult containing JPQL fragment and named parameters map
     */
    public JpqlResult build(final String alias, final Map<String, Object> where) {
        if (where == null || where.isEmpty()) {
            return new JpqlResult("", Map.of());
        }

        final Map<String, Object> params = new HashMap<>();
        final AtomicInteger counter = new AtomicInteger(0);
        final String jpql = buildInternal(alias, where, params, counter);
        return new JpqlResult(jpql, params);
    }

    /**
     * Builds a JPQL WHERE clause fragment with positional parameters.
     * Alternative API for use with positional parameter queries.
     *
     * @param alias The entity alias (e.g., "m" for "SELECT m FROM Message m")
     * @param where The where condition map from GraphQL
     * @param parameters List to collect parameter values (will be bound to query)
     * @param counter Tracks the positional parameter index
     * @return JPQL WHERE clause fragment (without "WHERE" keyword)
     */
    public String build(final String alias, final Map<String, Object> where,
                        final List<Object> parameters, final AtomicInteger counter) {
        if (where == null || where.isEmpty()) {
            return "";
        }

        final Map.Entry<String, Object> entry = where.entrySet().iterator().next();
        final String key = entry.getKey();
        final Object value = entry.getValue();

        return switch (key) {
            case "equals" -> buildEqualsPositional(alias, value, parameters, counter);
            case "unequals" -> buildNotEqualsPositional(alias, value, parameters, counter);
            case "greaterThan" -> buildGreaterThanPositional(alias, value, parameters, counter);
            case "lessThan" -> buildLessThanPositional(alias, value, parameters, counter);
            case "and" -> buildAndPositional(alias, (List<Map<String, Object>>) value, parameters, counter);
            case "or" -> buildOrPositional(alias, (List<Map<String, Object>>) value, parameters, counter);
            case "contains" -> buildContainsPositional(alias, (Map<String, Object>) value, parameters, counter);
            case "inside" -> buildInsidePositional(alias, (List<Double>) value, parameters, counter);
            default -> {
                if (!(value instanceof Map)) {
                    throw new AbortExecutionException("Expected nested condition for field: " + key);
                }
                final Map<String, Object> nestedWhere = (Map<String, Object>) value;
                if (nestedWhere.isEmpty()) {
                    throw new AbortExecutionException("Empty expression for field: " + key);
                }
                yield build(alias + "." + key, nestedWhere, parameters, counter);
            }
        };
    }

    // === Named parameter methods (for JpqlResult API) ===

    private String buildInternal(final String path, final Map<String, Object> where,
                                  final Map<String, Object> params, final AtomicInteger counter) {
        final Map.Entry<String, Object> entry = where.entrySet().iterator().next();
        final String key = entry.getKey();
        final Object value = entry.getValue();

        return switch (key) {
            case "equals" -> buildEqualsNamed(path, value, params, counter);
            case "unequals" -> buildNotEqualsNamed(path, value, params, counter);
            case "greaterThan" -> buildGreaterThanNamed(path, value, params, counter);
            case "lessThan" -> buildLessThanNamed(path, value, params, counter);
            case "and" -> buildAndNamed(path, (List<Map<String, Object>>) value, params, counter);
            case "or" -> buildOrNamed(path, (List<Map<String, Object>>) value, params, counter);
            case "contains" -> buildContainsNamed(path, (Map<String, Object>) value, params, counter);
            case "inside" -> buildInsideNamed(path, (List<Double>) value, params, counter);
            default -> {
                if (!(value instanceof Map)) {
                    throw new AbortExecutionException("Expected nested condition for field: " + key);
                }
                final Map<String, Object> nestedWhere = (Map<String, Object>) value;
                if (nestedWhere.isEmpty()) {
                    throw new AbortExecutionException("Empty expression for field: " + key);
                }
                yield buildInternal(path + "." + key, nestedWhere, params, counter);
            }
        };
    }

    private String buildEqualsNamed(final String path, final Object value,
                                     final Map<String, Object> params, final AtomicInteger counter) {
        if (value == null) {
            return path + " IS NULL";
        }
        final String paramName = "p" + counter.getAndIncrement();
        params.put(paramName, convertValue(value));
        return path + " = :" + paramName;
    }

    private String buildNotEqualsNamed(final String path, final Object value,
                                        final Map<String, Object> params, final AtomicInteger counter) {
        if (value == null) {
            return path + " IS NOT NULL";
        }
        final String paramName = "p" + counter.getAndIncrement();
        params.put(paramName, convertValue(value));
        return path + " <> :" + paramName;
    }

    private String buildGreaterThanNamed(final String path, final Object value,
                                          final Map<String, Object> params, final AtomicInteger counter) {
        final String paramName = "p" + counter.getAndIncrement();
        params.put(paramName, convertValue(value));
        return path + " > :" + paramName;
    }

    private String buildLessThanNamed(final String path, final Object value,
                                       final Map<String, Object> params, final AtomicInteger counter) {
        final String paramName = "p" + counter.getAndIncrement();
        params.put(paramName, convertValue(value));
        return path + " < :" + paramName;
    }

    private String buildAndNamed(final String alias, final List<Map<String, Object>> conditions,
                                  final Map<String, Object> params, final AtomicInteger counter) {
        if (conditions == null || conditions.isEmpty()) {
            return "";
        }
        final List<String> clauses = conditions.stream()
                .map(condition -> buildInternal(alias, condition, params, counter))
                .filter(clause -> !clause.isEmpty())
                .toList();
        if (clauses.isEmpty()) {
            return "";
        }
        return "(" + String.join(" AND ", clauses) + ")";
    }

    private String buildOrNamed(final String alias, final List<Map<String, Object>> conditions,
                                 final Map<String, Object> params, final AtomicInteger counter) {
        if (conditions == null || conditions.isEmpty()) {
            return "";
        }
        final List<String> clauses = conditions.stream()
                .map(condition -> buildInternal(alias, condition, params, counter))
                .filter(clause -> !clause.isEmpty())
                .toList();
        if (clauses.isEmpty()) {
            return "";
        }
        return "(" + String.join(" OR ", clauses) + ")";
    }

    private String buildContainsNamed(final String path, final Map<String, Object> condition,
                                       final Map<String, Object> params, final AtomicInteger counter) {
        final String elementAlias = "elem" + counter.getAndIncrement();
        final String innerCondition = buildInternal(elementAlias, condition, params, counter);
        return "EXISTS (SELECT 1 FROM " + path + " " + elementAlias + " WHERE " + innerCondition + ")";
    }

    private String buildInsideNamed(final String path, final List<Double> coordinates,
                                     final Map<String, Object> params, final AtomicInteger counter) {
        final String p1 = "p" + counter.getAndIncrement();
        final String p2 = "p" + counter.getAndIncrement();
        final String p3 = "p" + counter.getAndIncrement();
        final String p4 = "p" + counter.getAndIncrement();
        params.put(p1, coordinates.get(0));
        params.put(p2, coordinates.get(1));
        params.put(p3, coordinates.get(2));
        params.put(p4, coordinates.get(3));
        return String.format("(%s.x >= :%s AND %s.y >= :%s AND %s.x <= :%s AND %s.y <= :%s)",
                path, p1, path, p2, path, p3, path, p4);
    }

    // === Positional parameter methods ===

    private String buildEqualsPositional(final String path, final Object value,
                                          final List<Object> parameters, final AtomicInteger counter) {
        if (value == null) {
            return path + " IS NULL";
        }
        final int paramIndex = counter.getAndIncrement();
        parameters.add(convertValue(value));
        return path + " = ?" + paramIndex;
    }

    private String buildNotEqualsPositional(final String path, final Object value,
                                             final List<Object> parameters, final AtomicInteger counter) {
        if (value == null) {
            return path + " IS NOT NULL";
        }
        final int paramIndex = counter.getAndIncrement();
        parameters.add(convertValue(value));
        return path + " <> ?" + paramIndex;
    }

    private String buildGreaterThanPositional(final String path, final Object value,
                                               final List<Object> parameters, final AtomicInteger counter) {
        final int paramIndex = counter.getAndIncrement();
        parameters.add(convertValue(value));
        return path + " > ?" + paramIndex;
    }

    private String buildLessThanPositional(final String path, final Object value,
                                            final List<Object> parameters, final AtomicInteger counter) {
        final int paramIndex = counter.getAndIncrement();
        parameters.add(convertValue(value));
        return path + " < ?" + paramIndex;
    }

    private String buildAndPositional(final String alias, final List<Map<String, Object>> conditions,
                                       final List<Object> parameters, final AtomicInteger counter) {
        if (conditions == null || conditions.isEmpty()) {
            return "";
        }
        final List<String> clauses = conditions.stream()
                .map(condition -> build(alias, condition, parameters, counter))
                .filter(clause -> !clause.isEmpty())
                .toList();
        if (clauses.isEmpty()) {
            return "";
        }
        return "(" + String.join(" AND ", clauses) + ")";
    }

    private String buildOrPositional(final String alias, final List<Map<String, Object>> conditions,
                                      final List<Object> parameters, final AtomicInteger counter) {
        if (conditions == null || conditions.isEmpty()) {
            return "";
        }
        final List<String> clauses = conditions.stream()
                .map(condition -> build(alias, condition, parameters, counter))
                .filter(clause -> !clause.isEmpty())
                .toList();
        if (clauses.isEmpty()) {
            return "";
        }
        return "(" + String.join(" OR ", clauses) + ")";
    }

    private String buildContainsPositional(final String path, final Map<String, Object> condition,
                                            final List<Object> parameters, final AtomicInteger counter) {
        final String elementAlias = "elem" + counter.getAndIncrement();
        final String innerCondition = build(elementAlias, condition, parameters, counter);
        return "EXISTS (SELECT 1 FROM " + path + " " + elementAlias + " WHERE " + innerCondition + ")";
    }

    private String buildInsidePositional(final String path, final List<Double> coordinates,
                                          final List<Object> parameters, final AtomicInteger counter) {
        final int p1 = counter.getAndIncrement();
        final int p2 = counter.getAndIncrement();
        final int p3 = counter.getAndIncrement();
        final int p4 = counter.getAndIncrement();
        parameters.add(coordinates.get(0));
        parameters.add(coordinates.get(1));
        parameters.add(coordinates.get(2));
        parameters.add(coordinates.get(3));
        return String.format("(%s.x >= ?%d AND %s.y >= ?%d AND %s.x <= ?%d AND %s.y <= ?%d)",
                path, p1, path, p2, path, p3, path, p4);
    }

    private Object convertValue(final Object value) {
        if (value instanceof final OffsetDateTime odt) {
            return odt.toZonedDateTime();
        }
        if (enumConverter != null) {
            return enumConverter.convert(value);
        }
        return value;
    }
}
