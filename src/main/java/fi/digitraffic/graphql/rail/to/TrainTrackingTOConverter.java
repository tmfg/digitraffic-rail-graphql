package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessageTypeEnum;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTypeTO;
import jakarta.persistence.Tuple;

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
                entity.trackSectionCode,
                entity.nextTrackSectionCode,
                entity.previousTrackSectionCode,
                getType(entity.type),
                null, null, null, null, null);
    }

    /**
     * Converts a JPQL Tuple row to a TrainTrackingMessageTO.
     * Alias names must match the projection expression in TrainToTrainTrackingMessagesLink.
     */
    public TrainTrackingMessageTO convertProjection(final Tuple row) {
        return new TrainTrackingMessageTO(
                row.get("id", Long.class).intValue(),
                row.get("trainNumber", String.class),
                row.get("virtualDepartureDate", java.time.LocalDate.class),
                row.get("stationShortCode", String.class),
                row.get("nextStationShortCode", String.class),
                row.get("previousStationShortCode", String.class),
                row.get("version", Long.class).toString(),
                row.get("timestamp", java.time.ZonedDateTime.class),
                row.get("trackSectionCode", String.class),
                row.get("nextTrackSectionCode", String.class),
                row.get("previousTrackSectionCode", String.class),
                getType(row.get("type", TrainTrackingMessageTypeEnum.class)),
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
