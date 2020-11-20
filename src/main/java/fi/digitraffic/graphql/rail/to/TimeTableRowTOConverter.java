package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTimeTableRow;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.model.EstimateSourceTypeTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTypeTO;

@Component
public class TimeTableRowTOConverter extends BaseConverter<TimeTableRowTO> {
    @Override
    public TimeTableRowTO convert(Tuple tuple) {
        return new TimeTableRowTO(
                tuple.get(QTimeTableRow.timeTableRow.stationShortCode),
                tuple.get(QTimeTableRow.timeTableRow.stationUICCode),
                tuple.get(QTimeTableRow.timeTableRow.countryCode),
                convertTimeTableRowType(tuple.get(QTimeTableRow.timeTableRow.type)),
                tuple.get(QTimeTableRow.timeTableRow.trainStopping),
                tuple.get(QTimeTableRow.timeTableRow.commercialStop),
                tuple.get(QTimeTableRow.timeTableRow.commercialTrack),
                tuple.get(QTimeTableRow.timeTableRow.cancelled),
                tuple.get(QTimeTableRow.timeTableRow.scheduledTime),
                tuple.get(QTimeTableRow.timeTableRow.actualTime),
                nullableInt(tuple.get(QTimeTableRow.timeTableRow.differenceInMinutes)),
                tuple.get(QTimeTableRow.timeTableRow.liveEstimateTime),
                convertEstimateSource(tuple.get(QTimeTableRow.timeTableRow.estimateSource)),
                tuple.get(QTimeTableRow.timeTableRow.unknownDelay),
                tuple.get(QTimeTableRow.timeTableRow.id.attapId).intValue(),
                tuple.get(QTimeTableRow.timeTableRow.id.trainNumber).intValue(),
                tuple.get(QTimeTableRow.timeTableRow.id.departureDate),
                null,
                null);
    }

    private EstimateSourceTypeTO convertEstimateSource(TimeTableRow.EstimateSourceEnum estimateSourceEnum) {
        if (estimateSourceEnum == null) {
            return null;
        } else if (estimateSourceEnum == TimeTableRow.EstimateSourceEnum.COMBOCALC) {
            return EstimateSourceTypeTO.COMBOCALC;
        } else if (estimateSourceEnum == TimeTableRow.EstimateSourceEnum.LIIKE_AUTOMATIC) {
            return EstimateSourceTypeTO.LIIKE_AUTOMATIC;
        } else if (estimateSourceEnum == TimeTableRow.EstimateSourceEnum.LIIKE_USER) {
            return EstimateSourceTypeTO.LIIKE_USER;
        } else if (estimateSourceEnum == TimeTableRow.EstimateSourceEnum.MIKU_USER) {
            return EstimateSourceTypeTO.MIKU_USER;
        } else if (estimateSourceEnum == TimeTableRow.EstimateSourceEnum.UNKNOWN) {
            return EstimateSourceTypeTO.UNKNOWN;
        } else {
            throw new IllegalArgumentException(estimateSourceEnum.toString());
        }
    }

    public TimeTableRowTypeTO convertTimeTableRowType(TimeTableRow.TimeTableRowType type) {
        if (type == null) {
            return null;
        } else if (type == TimeTableRow.TimeTableRowType.ARRIVAL) {
            return TimeTableRowTypeTO.ARRIVAL;
        } else if (type == TimeTableRow.TimeTableRowType.DEPARTURE) {
            return TimeTableRowTypeTO.DEPARTURE;
        } else {
            throw new IllegalArgumentException(type.toString());
        }
    }
}
