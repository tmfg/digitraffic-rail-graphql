package fi.digitraffic.graphql.rail.querydsl;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SetPath;

import fi.digitraffic.graphql.rail.entities.StationTypeEnum;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessageTypeEnum;
import graphql.execution.AbortExecutionException;
import jakarta.annotation.PostConstruct;

@Service
public class WhereExpressionBuilder {
    private static Map<String, Operator> stringToOperationMap = Map.of(
            "greaterThan", Ops.GT,
            "lessThan", Ops.LT,
            "equals", Ops.EQ,
            "unequals", Ops.NE
    );

    private Map<String, Enum> enumValues = new HashMap<>();

    @PostConstruct
    public void setup() {
        final List<Enum<? extends Enum<?>>[]> valuess = List.of(
                TimeTableRow.EstimateSourceEnum.values(),
                Train.TimetableType.values(),
                TimeTableRow.TimeTableRowType.values(),
                StationTypeEnum.values(),
                TrainTrackingMessageTypeEnum.values()
        );

        for (final Enum[] values : valuess) {
            for (final Enum value : Lists.newArrayList(values)) {
                enumValues.put(value.name(), value);
            }
        }
    }

    public BooleanExpression create(BooleanExpression start, final PathBuilder path, final Map<String, Object> where) {
        final Map.Entry<String, Object> entry = where.entrySet().iterator().next();
        final String key = entry.getKey();
        final Object value = entry.getValue();

        if (key.equals("contains")) {
            start = contains(start, path, value);
        } else if (key.equals("and")) {
            start = and(start, path, (List<Map<String, Object>>) value);
        } else if (key.equals("or")) {
            start = or(start, path, (List<Map<String, Object>>) value);
        } else if (key.equals("equals")) {
            start = eq(path, value);
        } else if (key.equals("unequals")) {
            start = ne(path, value);
        } else if (key.equals("greaterThan")) {
            start = gt(path, value);
        } else if (key.equals("lessThan")) {
            start = lt(path, value);
        } else if (key.equals("inside")) {
            start = inside(path, (List<Double>) value);
        } else {
            final Map<String, Object> map = (Map<String, Object>) value;

            if (map.isEmpty()) {
                throw new AbortExecutionException("Empty expression " + key);
            }

            start = create(start, path.get(key), map);
        }

        return start;
    }

    private BooleanExpression contains(final BooleanExpression start, final PathBuilder path, final Object value) {
        final SetPath setPath = Expressions.setPath(path.getType(), EntityPathBase.class, path.getMetadata());
        final PathBuilder basePath = new PathBuilder(path.getType(), PathMetadataFactory.forCollectionAny(setPath));

        return create(null, basePath, (Map<String, Object>) value);
    }

    private Object convertToEnumOrDefault(final Object value) {
        final Enum anEnum = this.enumValues.get(value);
        if (anEnum != null) {
            return anEnum;
        } else {
            return value;
        }
    }

    private BooleanExpression inside(final PathBuilder path, final List<Double> value) {
        final BooleanExpression start;//TODO: Use Querydsl-spatial for this

        final List<Double> coordinates = value;
        final BooleanExpression expression1 = Expressions.asNumber(path.get("x")).goe(coordinates.get(0));
        final BooleanExpression expression2 = Expressions.asNumber(path.get("y")).goe(coordinates.get(1));
        final BooleanExpression expression3 = Expressions.asNumber(path.get("x")).loe(coordinates.get(2));
        final BooleanExpression expression4 = Expressions.asNumber(path.get("y")).loe(coordinates.get(3));
        start = expression1.and(expression2).and(expression3).and(expression4);
        return start;
    }

    private BooleanExpression lt(final PathBuilder path, final Object value) {
        final BooleanExpression start;
        if (value instanceof final Number number) {
            start = Expressions.asNumber(path).lt(number);
        } else if (value instanceof final ZonedDateTime zdt) {
            start = Expressions.asDateTime(path).lt(zdt);
        } else if (value instanceof final OffsetDateTime odt) {
            start = Expressions.asDateTime(path).lt(odt.toZonedDateTime());
        } else if (value instanceof final LocalDate ld) {
            start = Expressions.asDate(path).lt(ld);
        } else if (value instanceof final String string) {
            start = Expressions.asString(path).lt(string);
        } else {
            throw new IllegalArgumentException("Invalid lt type" + value.getClass().getSimpleName());
        }
        return start;
    }

    private BooleanExpression gt(final PathBuilder path, final Object value) {
        final BooleanExpression start;
        if (value instanceof final Number number) {
            start = Expressions.asNumber(path).gt(number);
        } else if (value instanceof final ZonedDateTime zdt) {
            start = Expressions.asDateTime(path).gt(zdt);
        } else if (value instanceof final OffsetDateTime odt) {
            start = Expressions.asDateTime(path).gt(odt.toZonedDateTime());
        } else if (value instanceof final LocalDate ld) {
            start = Expressions.asDate(path).gt(ld);
        } else if (value instanceof final String string) {
            start = Expressions.asString(path).gt(string);
        } else {
            throw new IllegalArgumentException("Invalid gt type " + value.getClass().getSimpleName());
        }
        return start;
    }

    private BooleanExpression eq(final PathBuilder path, final Object value) {
        final BooleanExpression start;
        if (value == null) {
            start = path.isNull();
        } else {
            start = path.eq(convertToEnumOrDefault(value));
        }
        return start;
    }

    private BooleanExpression ne(final PathBuilder path, final Object value) {
        final BooleanExpression start;
        if (value == null) {
            start = path.isNull();
        } else {
            start = path.ne(convertToEnumOrDefault(value));
        }
        return start;
    }

    private BooleanExpression or(BooleanExpression start, final PathBuilder path, final List<Map<String, Object>> value) {
        final List<Map<String, Object>> values = value;
        final List<BooleanExpression> expressions = new ArrayList<>();
        for (final Map<String, Object> childValue : values) {
            expressions.add(create(start, path, childValue));
        }

        BooleanExpression combinedExpression = null;
        for (final BooleanExpression expression : expressions) {
            if (combinedExpression == null) {
                combinedExpression = expression;
            } else {
                combinedExpression = combinedExpression.or(expression);
            }
        }
        start = combinedExpression;
        return start;
    }

    private BooleanExpression and(BooleanExpression start, final PathBuilder path, final List<Map<String, Object>> value) {
        final List<Map<String, Object>> values = value;
        final List<BooleanExpression> expressions = new ArrayList<>();
        for (final Map<String, Object> childValue : values) {
            expressions.add(create(start, path, childValue));
        }

        BooleanExpression combinedExpression = null;
        for (final BooleanExpression expression : expressions) {
            if (combinedExpression == null) {
                combinedExpression = expression;
            } else {
                combinedExpression = combinedExpression.and(expression);
            }
        }
        start = combinedExpression;
        return start;
    }
}
