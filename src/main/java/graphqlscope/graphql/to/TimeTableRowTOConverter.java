package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.TimeTableRow;
import graphqlscope.graphql.model.TimeTableRowTO;
import graphqlscope.graphql.model.TimeTableRowTypeTO;

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
                null);
    }
}
