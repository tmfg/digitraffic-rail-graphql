package fi.digitraffic.graphql.rail.factory;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;

@Component
public class TrainFactory {
    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TimeTableRowFactory ttrf;

    @Autowired
    private StationFactory stationFactory;

    @Transactional
    public Pair<Train, List<TimeTableRow>> createBaseTrain(TrainId id) {
        final String operatorShortCode = "test";
        final long trainCategoryId = 1;
        final long trainTypeId = 1;
        final String commuterLineID = "Z";
        final boolean runningCurrently = true;
        final boolean cancelled = false;
        final Long version = 1L;

        final LocalDate departureDate = id.departureDate;

        Train train = new Train();
        train.id = id;
        train.cancelled = cancelled;
        train.commuterLineID = commuterLineID;
        train.deleted = null;
        train.operatorShortCode = operatorShortCode;
        train.operatorUicCode = operatorShortCode.hashCode();
        train.runningCurrently = runningCurrently;
        train.timetableAcceptanceDate = ZonedDateTime.now();
        train.timetableType = Train.TimetableType.REGULAR;
        train.trainCategoryId = trainCategoryId;
        train.trainTypeId = trainTypeId;
        train.version = version;

        train = trainRepository.save(train);

        final ZonedDateTime now = ZonedDateTime.now().withYear(departureDate.getYear()).withMonth(departureDate.getMonthValue())
                .withDayOfMonth(departureDate.getDayOfMonth());
        List<TimeTableRow> timeTableRowList = new ArrayList<>();
        Station hkiStation = stationFactory.create("HKI", 1, "FI");
        timeTableRowList.add(ttrf.create(train, now.plusHours(1), now.plusHours(1).plusMinutes(1), hkiStation,
                TimeTableRow.TimeTableRowType.DEPARTURE));
        Station pslStation = stationFactory.create("PSL", 2, "FI");
        timeTableRowList.add(ttrf.create(train, now.plusHours(2), now.plusHours(2).plusMinutes(3), pslStation,
                TimeTableRow.TimeTableRowType.ARRIVAL));
        timeTableRowList.add(ttrf.create(train, now.plusHours(3), now.plusHours(3).plusMinutes(4), pslStation,
                TimeTableRow.TimeTableRowType.DEPARTURE));
        Station tpeStation = stationFactory.create("TPE", 3, "FI");
        timeTableRowList.add(ttrf.create(train, now.plusHours(4), now.plusHours(4).plusMinutes(5), tpeStation,
                TimeTableRow.TimeTableRowType.ARRIVAL));
        timeTableRowList.add(ttrf.create(train, now.plusHours(5), now.plusHours(5).plusMinutes(1), tpeStation,
                TimeTableRow.TimeTableRowType.DEPARTURE));
        Station jyStation = stationFactory.create("JY", 4, "FI");
        timeTableRowList.add(ttrf.create(train, now.plusHours(5), now.plusHours(5).plusMinutes(1), jyStation,
                TimeTableRow.TimeTableRowType.ARRIVAL));
        timeTableRowList.add(
                ttrf.create(train, now.plusHours(7), null, jyStation, TimeTableRow.TimeTableRowType.DEPARTURE));
        Station olStation = stationFactory.create("OL", 5, "FI");
        timeTableRowList.add(
                ttrf.create(train, now.plusHours(8), null, olStation, TimeTableRow.TimeTableRowType.ARRIVAL));

        return Pair.of(train, timeTableRowList);
    }
}
