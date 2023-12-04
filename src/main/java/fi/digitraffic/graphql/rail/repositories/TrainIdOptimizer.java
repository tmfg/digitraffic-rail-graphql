package fi.digitraffic.graphql.rail.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Multimaps;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QTrainId;
import fi.digitraffic.graphql.rail.entities.TrainId;

public class TrainIdOptimizer {
    public static BooleanExpression optimize(QTrainId qTrainId, List<TrainId> trainIds) {
        var departureDateMultiMap = Multimaps.index(trainIds, s -> s.departureDate);

        BooleanExpression expression = null;
        final var keys = departureDateMultiMap.keySet();
        for (final LocalDate localDate : keys) {
            var trainNumbers = departureDateMultiMap.get(localDate).stream().map(s -> s.trainNumber).collect(Collectors.toSet()).stream().sorted().collect(Collectors.toList());
            var expressionForDay = createExpressionForDay(qTrainId, localDate, trainNumbers);
            if (expression == null) {
                expression = expressionForDay;
            } else {
                expression = expression.or(expressionForDay);
            }
        }

        return expression;
    }

    private static BooleanExpression createExpressionForDay(final QTrainId qTrainId, final LocalDate key, final List<Long> trainNumbers) {
        return qTrainId.departureDate.eq(key).and(qTrainId.trainNumber.in(trainNumbers));
    }
}
