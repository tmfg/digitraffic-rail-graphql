package fi.digitraffic.graphql.rail.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Multimaps;
import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.entities.QStringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.QTimeTableRowId;
import fi.digitraffic.graphql.rail.entities.QTrainId;
import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.entities.TrainId;

public class TrainIdOptimizer {
    public static BooleanExpression optimize(final QTrainId qTrainId, final List<TrainId> trainIds) {
        return optimize(trainIds, s -> s.departureDate, s -> s.trainNumber,
                (localDate, trainNumbers) -> qTrainId.departureDate.eq(localDate).and(qTrainId.trainNumber.in(trainNumbers)));
    }

    public static BooleanExpression optimize(final QStringVirtualDepartureDateTrainId qTrainId,
                                             final List<StringVirtualDepartureDateTrainId> trainIds) {
        return optimize(trainIds, s -> s.virtualDepartureDate, s -> s.trainNumber,
                (localDate, trainNumbers) -> qTrainId.virtualDepartureDate.eq(localDate).and(qTrainId.trainNumber.in(trainNumbers)));
    }

    public static BooleanExpression optimize(final QTimeTableRowId timeTableRowId, final List<TimeTableRowId> timeTableRowIds) {
        return optimize(timeTableRowIds, s -> s.departureDate, s -> s.trainNumber,
                (localDate, trainNumbers) -> timeTableRowId.departureDate.eq(localDate)
                        .and(timeTableRowId.trainNumber.in(trainNumbers).and(timeTableRowId.in(timeTableRowIds))));
    }

    private static <TrainIdType, TrainNumberType> BooleanExpression optimize(
            final List<TrainIdType> trainIds,
            final Function<TrainIdType, LocalDate> departureDateProvider,
            final Function<TrainIdType, TrainNumberType> trainNumberProvider,
            final BiFunction<LocalDate, List<TrainNumberType>, BooleanExpression> dayExpressionProvider) {
        final var departureDateMultiMap = Multimaps.index(trainIds, departureDateProvider::apply);

        BooleanExpression expression = null;
        final var keys = departureDateMultiMap.keySet();
        for (final LocalDate localDate : keys) {
            final var trainNumbers =
                    departureDateMultiMap.get(localDate).stream().map(trainNumberProvider::apply).collect(Collectors.toSet()).stream().sorted()
                            .collect(Collectors.toList());
            final var expressionForDay = dayExpressionProvider.apply(localDate, trainNumbers);
            if (expression == null) {
                expression = expressionForDay;
            } else {
                expression = expression.or(expressionForDay);
            }
        }

        return expression;
    }
}
