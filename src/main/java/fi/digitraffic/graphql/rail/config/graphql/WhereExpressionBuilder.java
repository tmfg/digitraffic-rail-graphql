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
import fi.digitraffic.graphql.rail.entities.TrainRunningMessageTypeEnum;

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
            List<Map<String, Object>> values = (List<Map<String, Object>>) value;
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
        } else if (key.equals("or")) {
            List<Map<String, Object>> values = (List<Map<String, Object>>) value;
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
        } else if (key.equals("eq")) {
            if (value == null) {
                start = path.isNull();
            } else if (path.toString().endsWith("timetableType")) {
                start = path.eq(Train.TimetableType.valueOf(value.toString()));
            } else if (path.toString().endsWith("timeTableRowType")) {
                start = path.eq(TimeTableRow.TimeTableRowType.valueOf(value.toString()));
            } else if (path.toString().endsWith("stationType")) {
                start = path.eq(StationTypeEnum.valueOf(value.toString()));
            } else if (path.toString().endsWith("trainTrackingMessageType")) {
                start = path.eq(TrainRunningMessageTypeEnum.valueOf(value.toString()));
            } else {
                start = path.eq(value);
            }
        } else if (key.equals("gt")) {
            if (value instanceof Number) {
                start = Expressions.asNumber(path).gt((Number) value);
            } else if (value instanceof ZonedDateTime) {
                start = Expressions.asDateTime(path).gt((ZonedDateTime) value);
            } else if (value instanceof LocalDate) {
                start = Expressions.asDate(path).gt((LocalDate) value);
            } else {
                throw new IllegalArgumentException("Invalid gt type");
            }

        } else if (key.equals("lt")) {
            if (value instanceof Number) {
                start = Expressions.asNumber(path).lt((Number) value);
            } else if (value instanceof ZonedDateTime) {
                start = Expressions.asDateTime(path).lt((ZonedDateTime) value);
            } else if (value instanceof LocalDate) {
                start = Expressions.asDate(path).lt((LocalDate) value);
            } else {
                throw new IllegalArgumentException("Invalid gt type");
            }
        } else if (key.equals("inside")) {
            //TODO: Use Querydsl-spatial for this

            List<Double> coordinates = (List<Double>) value;
            BooleanExpression expression1 = Expressions.asNumber(path.get("x")).goe(coordinates.get(0));
            BooleanExpression expression2 = Expressions.asNumber(path.get("y")).goe(coordinates.get(1));
            BooleanExpression expression3 = Expressions.asNumber(path.get("x")).loe(coordinates.get(2));
            BooleanExpression expression4 = Expressions.asNumber(path.get("y")).loe(coordinates.get(3));
            start = expression1.and(expression2).and(expression3).and(expression4);
        } else {
            start = create(start, path.get(key), (Map<String, Object>) value);
        }

        return start;
    }
}
