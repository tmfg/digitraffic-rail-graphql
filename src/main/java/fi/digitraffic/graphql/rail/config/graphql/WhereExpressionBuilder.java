package fi.digitraffic.graphql.rail.config.graphql;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import fi.digitraffic.graphql.rail.entities.StationTypeEnum;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessageTypeEnum;

@Service
public class WhereExpressionBuilder {
    public BooleanExpression create(BooleanExpression start, PathBuilder path, Map<String, Object> where) {
        Map.Entry<String, Object> entry = where.entrySet().iterator().next();
        String key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof OffsetDateTime) {
            value = ((OffsetDateTime) value).toZonedDateTime();
        }
        if (key.equals("and")) {
            start = and(start, path, (List<Map<String, Object>>) value);
        } else if (key.equals("or")) {
            start = or(start, path, (List<Map<String, Object>>) value);
        } else if (key.equals("eq")) {
            start = eq(path, value);
        } else if (key.equals("ne")) {
            start = ne(path, value);
        } else if (key.equals("gt")) {
            start = gt(path, value);
        } else if (key.equals("lt")) {
            start = lt(path, value);
        } else if (key.equals("inside")) {
            start = inside(path, (List<Double>) value);
        } else {
            start = create(start, path.get(key), (Map<String, Object>) value);
        }

        return start;
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
        } else if (path.toString().endsWith("timetableType")) {
            start = path.eq(Train.TimetableType.valueOf(value.toString()));
        } else if (path.toString().endsWith("timeTableRow.type")) {
            start = path.eq(TimeTableRow.TimeTableRowType.valueOf(value.toString()));
        } else if (path.toString().endsWith("station.type")) {
            start = path.eq(StationTypeEnum.valueOf(value.toString()));
        } else if (path.toString().endsWith("trainTrackingMessageType")) {
            start = path.eq(TrainTrackingMessageTypeEnum.valueOf(value.toString()));
        } else if (path.toString().endsWith("trainTrackingMessage.type")) {
            start = path.eq(TrainTrackingMessageTypeEnum.valueOf(value.toString()));
        } else {
            start = path.eq(value);
        }
        return start;
    }

    private BooleanExpression ne(PathBuilder path, Object value) {
        BooleanExpression start;
        if (value == null) {
            start = path.isNull();
        } else if (path.toString().endsWith("timetableType")) {
            start = path.ne(Train.TimetableType.valueOf(value.toString()));
        } else if (path.toString().endsWith("timeTableRow.type")) {
            start = path.ne(TimeTableRow.TimeTableRowType.valueOf(value.toString()));
        } else if (path.toString().endsWith("station.type")) {
            start = path.ne(StationTypeEnum.valueOf(value.toString()));
        } else if (path.toString().endsWith("trainTrackingMessageType")) {
            start = path.ne(TrainTrackingMessageTypeEnum.valueOf(value.toString()));
        } else if (path.toString().endsWith("trainTrackingMessage.type")) {
            start = path.ne(TrainTrackingMessageTypeEnum.valueOf(value.toString()));
        } else {
            start = path.ne(value);
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
