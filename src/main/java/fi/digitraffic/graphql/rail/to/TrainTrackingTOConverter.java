package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainRunningMessageTypeEnum;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTypeTO;

@Component
public class TrainTrackingTOConverter {
    public TrainTrackingMessageTO convert(TrainTrackingMessage entity) {
        return new TrainTrackingMessageTO(
                entity.id.intValue(),
                entity.trainId.trainNumber,
                entity.trainId.virtualDepartureDate,
                entity.station,
                entity.nextStation,
                entity.previousStation,
                entity.version.toString(),
                entity.timestamp,
                entity.trackSection,
                entity.nextTrackSection,
                entity.previousTrackSection,
                getType(entity),
                null, null, null, null);
    }

    private TrainTrackingMessageTypeTO getType(TrainTrackingMessage entity) {
        if (entity.type == TrainRunningMessageTypeEnum.OCCUPY) {
            return TrainTrackingMessageTypeTO.OCCUPY;
        } else if (entity.type == TrainRunningMessageTypeEnum.RELEASE) {
            return TrainTrackingMessageTypeTO.RELEASE;
        } else {
            throw new IllegalArgumentException("Unknonwn TrainRunningMessageTypeEnum " + entity.type);
        }
    }
}
