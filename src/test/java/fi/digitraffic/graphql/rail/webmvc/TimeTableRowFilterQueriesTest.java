package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;


public class TimeTableRowFilterQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainRepository trainRepository;

//    @Test
//    public void nestedStringSearchShouldWork() throws Exception {
//        Pair<Train, List<TimeTableRow>> train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
//        Pair<Train, List<TimeTableRow>> train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2020, 9, 17)));
//        Pair<Train, List<TimeTableRow>> train68 = trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2020, 9, 17)));
//        Pair<Train, List<TimeTableRow>> train69 = trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2020, 9, 17)));
//
//        // HKI -> OL
//        timeTableRowRepository.delete(train67.getRight().get(1));
//        timeTableRowRepository.delete(train67.getRight().get(2));
//        timeTableRowRepository.delete(train67.getRight().get(3));
//        timeTableRowRepository.delete(train67.getRight().get(4));
//        timeTableRowRepository.delete(train67.getRight().get(5));
//        timeTableRowRepository.delete(train67.getRight().get(6));
//
//
//        ResultActions result = this.query("{  trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where: {timeTableRows: {station: {name: {equals: \\\"TPE\\\"}}}}) {    trainNumber    version    timeTableRows {      station {        name      }    }  }}");
//        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(3));
//
//        ResultActions result2 = this.query("{  trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where: {timeTableRows: {station: {name: {equals: \\\"HKI\\\"}}}}) {    trainNumber    version    timeTableRows {      station {        name      }    }  }}");
//        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(4));
//    }

    @Test
    public void childrenCollectionFilteringShouldWork() throws Exception {
        final Train train66 = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train67 = factoryService.getTrainFactory().createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train68 = factoryService.getTrainFactory().createBaseTrain(new TrainId(68L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train69 = factoryService.getTrainFactory().createBaseTrain(new TrainId(69L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train70 = factoryService.getTrainFactory().createBaseTrain(new TrainId(70L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train71 = factoryService.getTrainFactory().createBaseTrain(new TrainId(71L, LocalDate.of(2000, 1, 1))).getFirst();

        train66.timetableType = Train.TimetableType.ADHOC;
        train67.timetableType = Train.TimetableType.ADHOC;
        train68.timetableType = Train.TimetableType.ADHOC;
        train69.timetableType = Train.TimetableType.REGULAR;
        train70.timetableType = Train.TimetableType.REGULAR;
        train71.timetableType = Train.TimetableType.REGULAR;

        trainRepository.saveAll(List.of(train66, train67, train68, train69, train70, train71));

        factoryService.getTrainLocationFactory().create(1, 1, 11, train66);
        factoryService.getTrainLocationFactory().create(1, 1, 12, train66);
        factoryService.getTrainLocationFactory().create(1, 1, 13, train66);
        factoryService.getTrainLocationFactory().create(2, 3, 10, train67);
        factoryService.getTrainLocationFactory().create(2, 3, 10, train67);
        factoryService.getTrainLocationFactory().create(4, 4, 13, train68);
        factoryService.getTrainLocationFactory().create(3, 2, 14, train69);
        factoryService.getTrainLocationFactory().create(4, 3, 15, train70);
        factoryService.getTrainLocationFactory().create(5, 1, 16, train71);

        final ResultActions result = this.query("{  trainsByDepartureDate(departureDate: \"2000-01-01\", where: {timetableType: {equals: \"ADHOC\"}}) {    timetableType    timeTableRows {       type       scheduledTime       station {         shortCode       }       commercialTrack     }    trainLocations(where: {speed: {greaterThan: 11}}) {      speed    }  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainLocations.length()").value(2));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[1].trainLocations").doesNotExist());
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[2].trainLocations.length()").value(1));
    }
}
