package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;


public class AndOrFilterQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

    @Test
    public void AndOrShouldWork() throws Exception {
        createTestData();

        ResultActions result = this.query("{  trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where: {or: [{deleted: {eq: true}}, {cancelled: {eq: true}}]}) {    cancelled    deleted runningCurrently    trainNumber  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(6));

        ResultActions result2 = this.query("{  trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where: {and: [{deleted: {eq: true}}, {cancelled: {eq: false}}, {runningCurrently: {eq: false}}]}) {    cancelled    deleted runningCurrently    trainNumber  }}");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        ResultActions result3 = this.query("{  trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where: {      or: [    {deleted: {eq: false}},     {and: [{runningCurrently: {eq: false}}, {cancelled: {eq: false}}]}  ]   }) {    cancelled    deleted    runningCurrently    trainNumber  }}");
        result3.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(5));

        ResultActions result4 = this.query("{  trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where: {      and: [    {deleted: {eq: false}},     {or: [{runningCurrently: {eq: false}}, {cancelled: {eq: false}}]}  ]   }) {    cancelled    deleted    runningCurrently    trainNumber  }}");
        result4.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(3));
    }

    private void createTestData() {
        Pair<Train, List<TimeTableRow>> train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        Pair<Train, List<TimeTableRow>> train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2020, 9, 17)));
        Pair<Train, List<TimeTableRow>> train68 = trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2020, 9, 17)));
        Pair<Train, List<TimeTableRow>> train69 = trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2020, 9, 17)));
        Pair<Train, List<TimeTableRow>> train70 = trainFactory.createBaseTrain(new TrainId(70L, LocalDate.of(2020, 9, 17)));
        Pair<Train, List<TimeTableRow>> train71 = trainFactory.createBaseTrain(new TrainId(71L, LocalDate.of(2020, 9, 17)));
        Pair<Train, List<TimeTableRow>> train72 = trainFactory.createBaseTrain(new TrainId(72L, LocalDate.of(2020, 9, 17)));
        Pair<Train, List<TimeTableRow>> train73 = trainFactory.createBaseTrain(new TrainId(73L, LocalDate.of(2020, 9, 17)));

        train66.getLeft().deleted = true;
        train66.getLeft().cancelled = true;
        train66.getLeft().runningCurrently = true;

        train67.getLeft().deleted = true;
        train67.getLeft().cancelled = true;
        train66.getLeft().runningCurrently = false;

        train68.getLeft().deleted = true;
        train68.getLeft().cancelled = false;
        train66.getLeft().runningCurrently = true;

        train69.getLeft().deleted = true;
        train69.getLeft().cancelled = false;
        train69.getLeft().runningCurrently = false;

        train70.getLeft().deleted = false;
        train70.getLeft().cancelled = true;
        train70.getLeft().runningCurrently = true;

        train71.getLeft().deleted = false;
        train71.getLeft().cancelled = true;
        train71.getLeft().runningCurrently = false;

        train72.getLeft().deleted = false;
        train72.getLeft().cancelled = false;
        train72.getLeft().runningCurrently = true;

        train73.getLeft().deleted = false;
        train73.getLeft().cancelled = false;
        train73.getLeft().runningCurrently = false;

        trainRepository.saveAll(List.of(train66.getLeft(), train67.getLeft(), train68.getLeft(), train69.getLeft(), train70.getLeft(), train71.getLeft(), train72.getLeft(), train73.getLeft()));
    }
}
