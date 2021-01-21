package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QTrainTrackingMessage;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessageTypeEnum;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTypeTO;

@Component
public class TrainTrackingTOConverter {
    public TrainTrackingMessageTO convert(Tuple tuple) {
        return new TrainTrackingMessageTO(
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.id).intValue(),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.trainId.trainNumber),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.trainId.virtualDepartureDate),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.stationShortCode),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.nextStationShortCode),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.previousStationShortCode),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.version).toString(),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.timestamp),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.trackSectionCode),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.nextTrackSectionCode),
                tuple.get(QTrainTrackingMessage.trainTrackingMessage.previousTrackSectionCode),
                getType(tuple.get(QTrainTrackingMessage.trainTrackingMessage.type)),
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
