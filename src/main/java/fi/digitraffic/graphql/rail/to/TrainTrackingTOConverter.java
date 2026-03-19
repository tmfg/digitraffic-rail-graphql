package fi.digitraffic.graphql.rail.to;

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

    private TrainTrackingMessageTypeTO getType(TrainTrackingMessageTypeEnum type) {
        if (type == TrainTrackingMessageTypeEnum.OCCUPY) {
            return TrainTrackingMessageTypeTO.OCCUPY;
        } else if (type == TrainTrackingMessageTypeEnum.RELEASE) {
            return TrainTrackingMessageTypeTO.RELEASE;
        } else {
            throw new IllegalArgumentException("Unknonwn TrainRunningMessageTypeEnum " + type);
        }
    }
}
