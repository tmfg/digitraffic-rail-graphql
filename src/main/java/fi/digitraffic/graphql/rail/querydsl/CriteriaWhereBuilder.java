package fi.digitraffic.graphql.rail.querydsl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import graphql.execution.AbortExecutionException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

@Service
public class CriteriaWhereBuilder {

    private final EnumConverter enumConverter;

    public CriteriaWhereBuilder(final EnumConverter enumConverter) {
        this.enumConverter = enumConverter;
    }

    public <T> Specification<T> build(final Map<String, Object> where) {
        if (where == null || where.isEmpty()) {
            return Specification.where(null);
        }
        return (root, query, cb) -> create(root, where, cb);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate create(final Path<?> path, final Map<String, Object> where, final CriteriaBuilder cb) {
        final var entry = where.entrySet().iterator().next();
        final String key = entry.getKey();
        final Object value = entry.getValue();

        return switch (key) {
            case "and" -> cb.and(((List<Map<String, Object>>) value).stream()
                    .map(m -> create(path, m, cb)).toArray(Predicate[]::new));
            case "or" -> cb.or(((List<Map<String, Object>>) value).stream()
                    .map(m -> create(path, m, cb)).toArray(Predicate[]::new));
            case "equals" -> value == null ? cb.isNull(path) : cb.equal(path, convert(value));
            case "unequals" -> value == null ? cb.isNotNull(path) : cb.notEqual(path, convert(value));
            case "greaterThan" -> cb.greaterThan((Path<Comparable>) path, (Comparable) convert(value));
            case "lessThan" -> cb.lessThan((Path<Comparable>) path, (Comparable) convert(value));
            case "inside" -> {
                final var c = (List<Double>) value;
                yield cb.and(
                        cb.ge(path.get("x"), c.get(0)), cb.ge(path.get("y"), c.get(1)),
                        cb.le(path.get("x"), c.get(2)), cb.le(path.get("y"), c.get(3)));
            }
            case "contains" -> cb.isTrue(cb.literal(true)); // TODO: subquery
            default -> {
                final var nested = (Map<String, Object>) value;
                if (nested.isEmpty()) throw new AbortExecutionException("Empty expression: " + key);
                yield create(path.get(key), nested, cb);
            }
        };
    }

    private Object convert(final Object value) {
        if (value instanceof final OffsetDateTime odt) return odt.toZonedDateTime();
        return enumConverter.convert(value);
    }
}

