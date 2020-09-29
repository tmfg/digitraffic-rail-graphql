package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTimeTableRow;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
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
                convert(tuple.get(QTimeTableRow.timeTableRow.type)),
                tuple.get(QTimeTableRow.timeTableRow.trainStopping),
                tuple.get(QTimeTableRow.timeTableRow.commercialStop),
                tuple.get(QTimeTableRow.timeTableRow.commercialTrack),
                tuple.get(QTimeTableRow.timeTableRow.cancelled),
                tuple.get(QTimeTableRow.timeTableRow.scheduledTime),
                tuple.get(QTimeTableRow.timeTableRow.actualTime),
                tuple.get(QTimeTableRow.timeTableRow.id.attapId).intValue(),
                tuple.get(QTimeTableRow.timeTableRow.id.trainNumber).intValue(),
                tuple.get(QTimeTableRow.timeTableRow.id.departureDate),
                null,
                null);
    }

    public TimeTableRowTypeTO convert(TimeTableRow.TimeTableRowType type) {
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
