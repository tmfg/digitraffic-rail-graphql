package fi.digitraffic.graphql.rail.to;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessageTypeEnum;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTypeTO;

@Component
public class TrainTrackingTOConverter {

    public TrainTrackingMessageTO convertEntity(final TrainTrackingMessage entity) {
        return new TrainTrackingMessageTO(
                entity.id.intValue(),
                entity.trainId.trainNumber,
                entity.trainId.virtualDepartureDate,
                entity.stationShortCode,
                entity.nextStationShortCode,
                entity.previousStationShortCode,
                entity.version.toString(),
                entity.timestamp,
                entity.track_section,
                entity.nextTrackSectionCode,
                entity.previousTrackSectionCode,
                getType(entity.type),
                null, null, null, null, null);
    }

    private static final int PROJECTION_COLUMN_COUNT = 12;

    /**
     * Converts a JPQL projection row to a TrainTrackingMessageTO.
     * Row order must match the projection expression in TrainToTrainTrackingMessagesLink.
     */
    public TrainTrackingMessageTO convertProjection(final Object[] row) {
        if (row.length != PROJECTION_COLUMN_COUNT) {
            throw new IllegalStateException(
                    "Expected " + PROJECTION_COLUMN_COUNT + " projection columns but got " + row.length);
        }
        return new TrainTrackingMessageTO(
                ((Long) row[0]).intValue(),
                (String) row[1],
                (LocalDate) row[2],
                (String) row[3],
                (String) row[4],
                (String) row[5],
                ((Long) row[6]).toString(),
                (ZonedDateTime) row[7],
                (String) row[8],
                (String) row[9],
                (String) row[10],
                getType((TrainTrackingMessageTypeEnum) row[11]),
                null, null, null, null, null);
    }

    private TrainTrackingMessageTypeTO getType(TrainTrackingMessageTypeEnum type) {
        if (type == TrainTrackingMessageTypeEnum.OCCUPY) {
            return TrainTrackingMessageTypeTO.OCCUPY;
        } else if (type == TrainTrackingMessageTypeEnum.RELEASE) {
            return TrainTrackingMessageTypeTO.RELEASE;
        } else {
            throw new IllegalArgumentException("Unknown TrainTrackingMessageTypeEnum " + type);
        }
    }
}
