package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;
import fi.digitraffic.graphql.rail.repositories.TimeTableRowRepository;


public class TimeTableRowFilterQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

    @Autowired
    private TimeTableRowRepository timeTableRowRepository;

    @Test
    public void nestedStringSearchShouldWork() throws Exception {
        Pair<Train, List<TimeTableRow>> train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        Pair<Train, List<TimeTableRow>> train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2020, 9, 17)));
        Pair<Train, List<TimeTableRow>> train68 = trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2020, 9, 17)));
        Pair<Train, List<TimeTableRow>> train69 = trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2020, 9, 17)));

        // HKI -> OL
        timeTableRowRepository.delete(train67.getRight().get(1));
        timeTableRowRepository.delete(train67.getRight().get(2));
        timeTableRowRepository.delete(train67.getRight().get(3));
        timeTableRowRepository.delete(train67.getRight().get(4));
        timeTableRowRepository.delete(train67.getRight().get(5));
        timeTableRowRepository.delete(train67.getRight().get(6));

        List<TimeTableRow> timeTableRows = timeTableRowRepository.findAll();
        List<Station> stations = stationRepository.findAll();


        ResultActions result = this.query("{  trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where: {timeTableRows: {station: {name: {eq: \\\"TPE\\\"}}}}) {    trainNumber    version    timeTableRows {      station {        name      }    }  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(3));

        ResultActions result2 = this.query("{  trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where: {timeTableRows: {station: {name: {eq: \\\"HKI\\\"}}}}) {    trainNumber    version    timeTableRows {      station {        name      }    }  }}");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(4));
    }
}
