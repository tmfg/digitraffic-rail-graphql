package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTrainTrackingMessage;
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
                getType(entity.type),
                null, null, null, null);
    }

    public TrainTrackingMessageTO convert(Tuple tuple) {
        return new TrainTrackingMessageTO(
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.id).intValue(),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.trainId.trainNumber),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.trainId.virtualDepartureDate),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.station),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.nextStation),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.previousStation),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.version).toString(),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.timestamp),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.trackSection),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.nextTrackSection),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.previousTrackSection),
                getType(tuple.get(QTrainTrackingMessage.trainTrackingMessage.type)),
                null, null, null, null);
    }

    private TrainTrackingMessageTypeTO getType(TrainRunningMessageTypeEnum type) {
        if (type == TrainRunningMessageTypeEnum.OCCUPY) {
            return TrainTrackingMessageTypeTO.OCCUPY;
        } else if (type == TrainRunningMessageTypeEnum.RELEASE) {
            return TrainTrackingMessageTypeTO.RELEASE;
        } else {
            throw new IllegalArgumentException("Unknonwn TrainRunningMessageTypeEnum " + type);
        }
    }
}
