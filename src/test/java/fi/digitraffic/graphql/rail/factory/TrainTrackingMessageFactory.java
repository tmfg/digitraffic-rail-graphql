package fi.digitraffic.graphql.rail.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessageTypeEnum;
import fi.digitraffic.graphql.rail.repositories.TrainTrackingMessageRepository;

@Component
public class TrainTrackingMessageFactory {

    @Autowired
    private TrainTrackingMessageRepository trainTrackingMessageRepository;

    @Transactional
    public TrainTrackingMessage create(Train train) {
        final TrainTrackingMessage ttr = new TrainTrackingMessage();
        ttr.id=1L;
        ttr.trainId = new StringVirtualDepartureDateTrainId(train.id.trainNumber.toString(), train.id.departureDate);
        ttr.departureDate = train.id.departureDate;
        ttr.track_section = "A";
        ttr.nextTrackSectionCode = "B";
        ttr.previousTrackSectionCode = "C";
        ttr.stationShortCode = "D";
        ttr.nextStationShortCode = "E";
        ttr.previousStationShortCode = "F";
        ttr.timestamp = train.timetableAcceptanceDate;
        ttr.type = TrainTrackingMessageTypeEnum.OCCUPY;
        ttr.version = 1L;

        return trainTrackingMessageRepository.save(ttr);
    }
}
