package fi.digitraffic.graphql.rail.queries;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import graphql.execution.AbortExecutionException;

/**
 * Builds WHERE clause strings from a nested Map structure (GraphQL input).
 *
 * Example input: {"trainNumber": {"equals": 123}}
 * Example output: "e.trainNumber = :p0" with params {p0: 123}
 */
@Service
public class JpqlWhereBuilder {

    public record JpqlResult(String jpql, Map<String, Object> params) {}

    private final EnumConverter enumConverter;

    @Autowired
    public JpqlWhereBuilder(final EnumConverter enumConverter) {
        this.enumConverter = enumConverter;
    }

    /**
     * Constructor for testing without EnumConverter.
     */
    public JpqlWhereBuilder() {
        this.enumConverter = null;
    }


    public JpqlResult build(final String alias, final Map<String, Object> where) {
        if (where == null || where.isEmpty()) {
            return new JpqlResult("", Map.of());
        }
        final var ctx = new BuildContext();
        return new JpqlResult(buildInternal(alias, where, ctx), ctx.params);
    }

    private static class BuildContext {
        final Map<String, Object> params = new HashMap<>();
        int paramCounter = 0;
        int aliasCounter = 0;
        String nextParam() { return "p" + paramCounter++; }
        String nextSubqueryAlias() { return "sub" + aliasCounter++; }
    }

    @SuppressWarnings("unchecked")
    private String buildInternal(final String path, final Map<String, Object> where, final BuildContext ctx) {
        // Process all entries and combine with AND (multiple conditions at same level)
        final java.util.List<String> clauses = new java.util.ArrayList<>();

        for (final var entry : where.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();

            final String clause = switch (key) {
                case "equals" -> comparison(path, value, "=", "IS NULL", ctx);
                case "unequals" -> comparison(path, value, "<>", "IS NOT NULL", ctx);
                case "greaterThan" -> comparison(path, value, ">", null, ctx);
                case "lessThan" -> comparison(path, value, "<", null, ctx);
                case "and" -> logical((List<Map<String, Object>>) value, " AND ", path, ctx);
                case "or" -> logical((List<Map<String, Object>>) value, " OR ", path, ctx);
                case "contains" -> contains(path, (Map<String, Object>) value, ctx);
                case "inside" -> inside(path, (List<Double>) value, ctx);
                default -> {
                    if (!(value instanceof final Map<?, ?> nested) || nested.isEmpty()) {
                        throw new AbortExecutionException("Invalid expression: " + key);
                    }
                    JpqlSafeIdentifier.validate(key);
                    yield buildInternal(path + "." + key, (Map<String, Object>) nested, ctx);
                }
            };

            if (!clause.isEmpty()) {
                clauses.add(clause);
            }
        }

        if (clauses.isEmpty()) {
            return "";
        }
        if (clauses.size() == 1) {
            return clauses.get(0);
        }
        // Multiple conditions at same level are ANDed together
        return "(" + String.join(" AND ", clauses) + ")";
    }

    private String comparison(final String path, final Object value, final String op,
                               final String nullOp, final BuildContext ctx) {
        final String resolvedPath = resolveJpaPath(path);
        if (value == null) {
            if (nullOp == null) {
                throw new AbortExecutionException("Null value not supported for this operator");
            }
            return resolvedPath + " " + nullOp;
        }
        final String param = ctx.nextParam();
        ctx.params.put(param, convertValue(value));
        return resolvedPath + " " + op + " :" + param;
    }

    private String logical(final List<Map<String, Object>> conditions, final String op,
                            final String path, final BuildContext ctx) {
        if (conditions == null || conditions.isEmpty()) {
            return "";
        }
        final var clauses = conditions.stream()
                .map(c -> buildInternal(path, c, ctx))
                .filter(s -> !s.isEmpty())
                .toList();
        return clauses.isEmpty() ? "" : "(" + String.join(op, clauses) + ")";
    }

    /**
     * Handles 'contains' operator for collection filtering.
     *
     * Generates an EXISTS subquery to check if any element in the collection matches.
     * Input: path = "e.timeTableRows", condition = {station: {type: {equals: "STATION"}}}
     * Output: "EXISTS (SELECT sub0 FROM e.timeTableRows sub0 WHERE sub0.station.type = :p0)"
     *
     * Hibernate 6+ does not allow implicit joins through plural (collection) paths,
     * so an explicit subquery is required.
     */
    private String contains(final String path, final Map<String, Object> condition, final BuildContext ctx) {
        final String resolvedPath = resolveJpaPath(path);
        final String subAlias = ctx.nextSubqueryAlias();
        final String innerCondition = buildInternal(subAlias, condition, ctx);
        if (innerCondition.isEmpty()) {
            return "";
        }
        return "EXISTS (SELECT " + subAlias + " FROM " + resolvedPath + " " + subAlias + " WHERE " + innerCondition + ")";
    }

    private String inside(final String path, final List<Double> coords, final BuildContext ctx) {
        final String resolvedPath = resolveJpaPath(path);
        if (coords == null || coords.size() != 4) {
            throw new AbortExecutionException(
                    "'inside' requires exactly 4 coordinates [xMin, yMin, xMax, yMax], got "
                            + (coords == null ? "null" : coords.size()));
        }
        final String[] p = {ctx.nextParam(), ctx.nextParam(), ctx.nextParam(), ctx.nextParam()};
        for (int i = 0; i < 4; i++) {
            ctx.params.put(p[i], coords.get(i));
        }
        return String.format("(%s.x >= :%s AND %s.y >= :%s AND %s.x <= :%s AND %s.y <= :%s)",
                resolvedPath, p[0], resolvedPath, p[1], resolvedPath, p[2], resolvedPath, p[3]);
    }

    /**
     * GraphQL exposes transient nested DTO-like fields (e.g. video.text.fi) that do not exist as
     * JPA paths. Translate those to actual mapped entity fields used in JPQL.
     */
    private String resolveJpaPath(final String path) {
        return path
                .replace(".video.text.fi", ".video.textFi")
                .replace(".video.text.sv", ".video.textSv")
                .replace(".video.text.en", ".video.textEn")
                .replace(".audio.text.fi", ".audio.textFi")
                .replace(".audio.text.sv", ".audio.textSv")
                .replace(".audio.text.en", ".audio.textEn")
                .replace(".video.deliveryRules.deliveryType", ".video.deliveryType")
                .replace(".video.deliveryRules.startDateTime", ".video.startDateTime")
                .replace(".video.deliveryRules.endDateTime", ".video.endDateTime")
                .replace(".video.deliveryRules.startTime", ".video.startTime")
                .replace(".video.deliveryRules.endTime", ".video.endTime")
                .replace(".video.deliveryRules.weekDays", ".video.weekDays")
                .replace(".audio.deliveryRules.deliveryType", ".audio.deliveryType")
                .replace(".audio.deliveryRules.eventType", ".audio.eventType")
                .replace(".audio.deliveryRules.startDateTime", ".audio.startDateTime")
                .replace(".audio.deliveryRules.endDateTime", ".audio.endDateTime")
                .replace(".audio.deliveryRules.startTime", ".audio.startTime")
                .replace(".audio.deliveryRules.endTime", ".audio.endTime")
                .replace(".audio.deliveryRules.weekDays", ".audio.weekDays")
                .replace(".audio.deliveryRules.deliveryAt", ".audio.deliveryAt")
                .replace(".audio.deliveryRules.repetitions", ".audio.repetitions")
                .replace(".audio.deliveryRules.repeatEvery", ".audio.repeatEvery");
    }

    private Object convertValue(final Object value) {
        if (value instanceof final OffsetDateTime odt) {
            return odt.toZonedDateTime();
        }
        return enumConverter != null ? enumConverter.convert(value) : value;
    }
}
