package fi.digitraffic.graphql.rail.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Multimaps;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QStringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.QTrainId;
import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.TrainId;

public class TrainIdOptimizer {
    public static BooleanExpression optimize(QTrainId qTrainId, List<TrainId> trainIds) {
        return optimize(trainIds, s -> s.departureDate, s -> s.trainNumber, (localDate, trainNumbers) -> qTrainId.departureDate.eq(localDate).and(qTrainId.trainNumber.in(trainNumbers)));
    }

    public static BooleanExpression optimize(QStringVirtualDepartureDateTrainId qTrainId, List<StringVirtualDepartureDateTrainId> trainIds) {
        return optimize(trainIds, s -> s.virtualDepartureDate, s -> s.trainNumber, (localDate, trainNumbers) -> qTrainId.virtualDepartureDate.eq(localDate).and(qTrainId.trainNumber.in(trainNumbers)));
    }

    private static <TrainIdType,TrainNumberType> BooleanExpression optimize(
        List<TrainIdType> trainIds,
        Function<TrainIdType,LocalDate> departureDateProvider,
        Function<TrainIdType,TrainNumberType> trainNumberProvider,
        BiFunction<LocalDate, List<TrainNumberType>, BooleanExpression> dayExpressionProvider) {
        var departureDateMultiMap = Multimaps.index(trainIds, s -> departureDateProvider.apply(s));

        BooleanExpression expression = null;
        final var keys = departureDateMultiMap.keySet();
        for (final LocalDate localDate : keys) {
            var trainNumbers = departureDateMultiMap.get(localDate).stream().map(s -> trainNumberProvider.apply(s)).collect(Collectors.toSet()).stream().sorted().collect(Collectors.toList());
            var expressionForDay = dayExpressionProvider.apply(localDate, trainNumbers);
            if (expression == null) {
                expression = expressionForDay;
            } else {
                expression = expression.or(expressionForDay);
            }
        }

        return expression;
    }
}
