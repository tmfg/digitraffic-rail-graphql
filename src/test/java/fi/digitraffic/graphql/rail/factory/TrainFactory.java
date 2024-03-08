package fi.digitraffic.graphql.rail.factory;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainCategory;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.entities.TrainType;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class TrainFactory {
    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TimeTableRowFactory ttrf;

    @Autowired
    private StationFactory stationFactory;

    @PersistenceContext
    private EntityManager entityManager;

    public Pair<Train, List<TimeTableRow>> createBaseTrain(final int trainNumber, final LocalDate departureDate) {
        final TrainId id = new TrainId(trainNumber, departureDate);
        final ZonedDateTime startTime = ZonedDateTime.now()
            .withYear(id.departureDate.getYear())
            .withMonth(id.departureDate.getMonthValue())
            .withDayOfMonth(id.departureDate.getDayOfMonth());

        return createBaseTrain(id, startTime);
    }

    public Pair<Train, List<TimeTableRow>> createBaseTrain(final TrainId id) {
        final ZonedDateTime startTime = ZonedDateTime.now()
            .withYear(id.departureDate.getYear())
            .withMonth(id.departureDate.getMonthValue())
            .withDayOfMonth(id.departureDate.getDayOfMonth());

        return createBaseTrain(id, startTime);
    }

    @Transactional
    public Pair<Train, List<TimeTableRow>> createBaseTrain(final TrainId id, final ZonedDateTime startTime) {
        TrainCategory trainCategory = new TrainCategory();
        trainCategory.name = "Test TrainCategory";
        trainCategory.id = 1L;
        entityManager.merge(trainCategory);

        TrainType trainType = new TrainType();
        trainType.name = "Test TrainType";
        trainType.trainCategoryId = 1L;
        trainType.id = 1L;
        entityManager.merge(trainType);
        
        final String operatorShortCode = "test";
        final String commuterLineID = "Z";
        final boolean runningCurrently = true;
        final boolean cancelled = false;
        final Long version = 1L;

        Train train = new Train();
        train.id = id;
        train.cancelled = cancelled;
        train.commuterLineid = commuterLineID;
        train.deleted = null;
        train.operatorShortCode = operatorShortCode;
        train.operatorUicCode = operatorShortCode.hashCode();
        train.runningCurrently = runningCurrently;
        train.timetableAcceptanceDate = ZonedDateTime.now();
        train.timetableType = Train.TimetableType.REGULAR;
        train.trainCategoryId = trainCategory.id;
        train.trainTypeId = trainType.id;
        train.version = version;

        train = trainRepository.save(train);

        final List<TimeTableRow> timeTableRowList = new ArrayList<>();
        final Station hkiStation = stationFactory.create("HKI", 1, "FI");
        timeTableRowList.add(ttrf.create(train, startTime.plusHours(1), startTime.plusHours(1).plusMinutes(1), hkiStation,
            TimeTableRow.TimeTableRowType.DEPARTURE));
        final Station pslStation = stationFactory.create("PSL", 2, "FI");
        timeTableRowList.add(ttrf.create(train, startTime.plusHours(2), startTime.plusHours(2).plusMinutes(3), pslStation,
            TimeTableRow.TimeTableRowType.ARRIVAL));
        timeTableRowList.add(ttrf.create(train, startTime.plusHours(3), startTime.plusHours(3).plusMinutes(4), pslStation,
            TimeTableRow.TimeTableRowType.DEPARTURE));
        final Station tpeStation = stationFactory.create("TPE", 3, "FI");
        timeTableRowList.add(ttrf.create(train, startTime.plusHours(4), startTime.plusHours(4).plusMinutes(5), tpeStation,
            TimeTableRow.TimeTableRowType.ARRIVAL));
        timeTableRowList.add(ttrf.create(train, startTime.plusHours(5), startTime.plusHours(5).plusMinutes(1), tpeStation,
            TimeTableRow.TimeTableRowType.DEPARTURE));
        final Station jyStation = stationFactory.create("JY", 4, "FI");
        timeTableRowList.add(ttrf.create(train, startTime.plusHours(5), startTime.plusHours(5).plusMinutes(1), jyStation,
            TimeTableRow.TimeTableRowType.ARRIVAL));
        timeTableRowList.add(
            ttrf.create(train, startTime.plusHours(7), null, jyStation, TimeTableRow.TimeTableRowType.DEPARTURE));
        final Station olStation = stationFactory.create("OL", 5, "FI");
        timeTableRowList.add(
            ttrf.create(train, startTime.plusHours(8), null, olStation, TimeTableRow.TimeTableRowType.ARRIVAL));

        return Pair.of(train, timeTableRowList);
    }
}
