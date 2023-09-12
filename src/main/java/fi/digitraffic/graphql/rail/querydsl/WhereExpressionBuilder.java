package fi.digitraffic.graphql.rail.querydsl;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

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

@Service
public class WhereExpressionBuilder {
    private Map<String, Operator> stringToOperationMap = Map.of(
            "greaterThan", Ops.GT,
            "lessThan", Ops.LT,
            "equals", Ops.EQ,
            "unequals", Ops.NE
    );

    private Map<String, Enum> enumValues = new HashMap<>();

    @PostConstruct
    public void setup() {
        List<Enum<? extends Enum<?>>[]> valuess = List.of(
                TimeTableRow.EstimateSourceEnum.values(),
                Train.TimetableType.values(),
                TimeTableRow.TimeTableRowType.values(),
                StationTypeEnum.values(),
                TrainTrackingMessageTypeEnum.values()
        );

        for (Enum[] values : valuess) {
            for (Enum value : Lists.newArrayList(values)) {
                enumValues.put(value.name(), value);
            }
        }
    }

    public BooleanExpression create(BooleanExpression start, PathBuilder path, Map<String, Object> where) {
        Map.Entry<String, Object> entry = where.entrySet().iterator().next();
        String key = entry.getKey();
        Object value = entry.getValue();

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
            start = create(start, path.get(key), (Map<String, Object>) value);
        }

        return start;
    }

    private BooleanExpression contains(BooleanExpression start, PathBuilder path, Object value) {
        SetPath setPath = Expressions.setPath(path.getType(), EntityPathBase.class, path.getMetadata());
        PathBuilder basePath = new PathBuilder(path.getType(), PathMetadataFactory.forCollectionAny(setPath));

        return create(null, basePath,(Map<String, Object>) value);
    }

    private Object convertToEnumOrDefault(Object value) {
        Enum anEnum = this.enumValues.get(value);
        if (anEnum != null) {
            return anEnum;
        } else {
            return value;
        }
    }

    private BooleanExpression inside(PathBuilder path, List<Double> value) {
        BooleanExpression start;//TODO: Use Querydsl-spatial for this

        List<Double> coordinates = value;
        BooleanExpression expression1 = Expressions.asNumber(path.get("x")).goe(coordinates.get(0));
        BooleanExpression expression2 = Expressions.asNumber(path.get("y")).goe(coordinates.get(1));
        BooleanExpression expression3 = Expressions.asNumber(path.get("x")).loe(coordinates.get(2));
        BooleanExpression expression4 = Expressions.asNumber(path.get("y")).loe(coordinates.get(3));
        start = expression1.and(expression2).and(expression3).and(expression4);
        return start;
    }

    private BooleanExpression lt(PathBuilder path, Object value) {
        BooleanExpression start;
        if (value instanceof Number) {
            start = Expressions.asNumber(path).lt((Number) value);
        } else if (value instanceof ZonedDateTime) {
            start = Expressions.asDateTime(path).lt((ZonedDateTime) value);
        } else if (value instanceof LocalDate) {
            start = Expressions.asDate(path).lt((LocalDate) value);
        } else {
            throw new IllegalArgumentException("Invalid gt type");
        }
        return start;
    }

    private BooleanExpression gt(PathBuilder path, Object value) {
        BooleanExpression start;
        if (value instanceof Number) {
            start = Expressions.asNumber(path).gt((Number) value);
        } else if (value instanceof ZonedDateTime) {
            start = Expressions.asDateTime(path).gt((ZonedDateTime) value);
        } else if (value instanceof LocalDate) {
            start = Expressions.asDate(path).gt((LocalDate) value);
        } else {
            throw new IllegalArgumentException("Invalid gt type");
        }
        return start;
    }

    private BooleanExpression eq(PathBuilder path, Object value) {
        BooleanExpression start;
        if (value == null) {
            start = path.isNull();
        } else {
            start = path.eq(convertToEnumOrDefault(value));
        }
        return start;
    }

    private BooleanExpression ne(PathBuilder path, Object value) {
        BooleanExpression start;
        if (value == null) {
            start = path.isNull();
        } else {
            start = path.ne(convertToEnumOrDefault(value));
        }
        return start;
    }

    private BooleanExpression or(BooleanExpression start, PathBuilder path, List<Map<String, Object>> value) {
        List<Map<String, Object>> values = value;
        List<BooleanExpression> expressions = new ArrayList<>();
        for (Map<String, Object> childValue : values) {
            expressions.add(create(start, path, childValue));
        }

        BooleanExpression combinedExpression = null;
        for (BooleanExpression expression : expressions) {
            if (combinedExpression == null) {
                combinedExpression = expression;
            } else {
                combinedExpression = combinedExpression.or(expression);
            }
        }
        start = combinedExpression;
        return start;
    }

    private BooleanExpression and(BooleanExpression start, PathBuilder path, List<Map<String, Object>> value) {
        List<Map<String, Object>> values = value;
        List<BooleanExpression> expressions = new ArrayList<>();
        for (Map<String, Object> childValue : values) {
            expressions.add(create(start, path, childValue));
        }

        BooleanExpression combinedExpression = null;
        for (BooleanExpression expression : expressions) {
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
