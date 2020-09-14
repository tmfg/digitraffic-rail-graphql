package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTypeTO;

@Component
public class TimeTableRowTOConverter {
    public TimeTableRowTO convert(TimeTableRow entity) {
        return new TimeTableRowTO(
                entity.stationShortCode,
                entity.stationUICCode,
                entity.countryCode,
                entity.type.equals(TimeTableRow.TimeTableRowType.ARRIVAL) ? TimeTableRowTypeTO.ARRIVAL : TimeTableRowTypeTO.DEPARTURE,
                entity.trainStopping,
                entity.commercialStop,
                entity.commercialTrack,
                entity.cancelled,
                entity.scheduledTime,
                entity.actualTime,
                entity.id.attapId.intValue(),
                entity.id.trainNumber.intValue(),
                entity.id.departureDate,
                null,
                null);
    }
}
