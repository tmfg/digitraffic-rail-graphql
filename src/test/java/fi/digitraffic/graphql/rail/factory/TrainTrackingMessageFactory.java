package fi.digitraffic.graphql.rail.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessageTypeEnum;
import fi.digitraffic.graphql.rail.repositories.TrainTrackingMessageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class TrainTrackingMessageFactory {

    @Autowired
    private TrainTrackingMessageRepository trainTrackingMessageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private long idSequence = 1L;

    @Transactional
    public TrainTrackingMessage create(Train train) {
        return createWithTrackSection(train, "TEST_UNIQUE");
    }

    @Transactional
    public TrainTrackingMessage createWithTrackSection(Train train, String trackSectionCode) {
        final TrainTrackingMessage ttr = new TrainTrackingMessage();
        ttr.id = idSequence++;
        ttr.trainId = new StringVirtualDepartureDateTrainId(train.id.trainNumber.toString(), train.id.departureDate);
        ttr.departureDate = train.id.departureDate;
        ttr.track_section = trackSectionCode;
        ttr.nextTrackSectionCode = null;
        ttr.previousTrackSectionCode = null;
        ttr.stationShortCode = "TEST99";
        ttr.nextStationShortCode = null;
        ttr.previousStationShortCode = null;
        ttr.timestamp = train.timetableAcceptanceDate;
        ttr.type = TrainTrackingMessageTypeEnum.OCCUPY;
        ttr.version = 1L;

        return trainTrackingMessageRepository.save(ttr);
    }

    @Transactional
    public TrainTrackingMessage createWithNonNumericTrainNumber(final Train train, final String trainNumber) {
        final long id = idSequence++;
        entityManager.createNativeQuery(
                "INSERT INTO train_running_message (id, version, timestamp, train_number, departure_date, track_section, station, type) VALUES (?, ?, NOW(), ?, ?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, 1L)
                .setParameter(3, trainNumber)
                .setParameter(4, train.id.departureDate)
                .setParameter(5, "TEST_UNIQUE")
                .setParameter(6, "TEST99")
                .setParameter(7, TrainTrackingMessageTypeEnum.OCCUPY.ordinal())
                .executeUpdate();
        return null;
    }
}
