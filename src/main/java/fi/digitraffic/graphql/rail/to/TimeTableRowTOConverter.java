package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.model.EstimateSourceTypeTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTypeTO;

@Component
public class TimeTableRowTOConverter extends BaseConverter {

    public TimeTableRowTO convertEntity(final TimeTableRow entity) {
        return new TimeTableRowTO(
                entity.stationShortCode,
                entity.stationUICCode,
                entity.countryCode,
                convertTimeTableRowType(entity.type),
                entity.trainStopping,
                entity.commercialStop,
                entity.commercialTrack,
                entity.cancelled,
                entity.scheduledTime,
                entity.actualTime,
                nullableInt(entity.differenceInMinutes),
                entity.liveEstimateTime,
                convertEstimateSource(entity.estimateSource),
                entity.unknownDelay,
                entity.stopSector,
                entity.id.attapId.intValue(),
                entity.id.trainNumber.intValue(),
                entity.id.departureDate,
                null,
                null,
                null
        );
    }

    private EstimateSourceTypeTO convertEstimateSource(final TimeTableRow.EstimateSourceEnum estimateSourceEnum) {
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

    public TimeTableRowTypeTO convertTimeTableRowType(final TimeTableRow.TimeTableRowType type) {
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
