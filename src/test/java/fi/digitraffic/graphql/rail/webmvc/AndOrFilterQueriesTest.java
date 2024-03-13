package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.FactoryService;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;


public class AndOrFilterQueriesTest extends BaseWebMVCTest {
    @Autowired
    private FactoryService factoryService;

    @Autowired
    private TrainRepository trainRepository;

    @Test
    public void AndOrShouldWork() throws Exception {
        createTestData();

        final ResultActions result = this.query("{  trainsByDepartureDate(departureDate: \"2020-09-17\", where: {or: [{deleted: {equals: true}}, {cancelled: {equals: true}}]}) {    cancelled    deleted runningCurrently    trainNumber  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(6));

        final ResultActions result2 = this.query("{  trainsByDepartureDate(departureDate: \"2020-09-17\", where: {and: [{deleted: {equals: true}}, {cancelled: {equals: false}}, {runningCurrently: {equals: false}}]}) {    cancelled    deleted runningCurrently    trainNumber  }}");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        final ResultActions result3 = this.query("{  trainsByDepartureDate(departureDate: \"2020-09-17\", where: {      or: [    {deleted: {equals: false}},     {and: [{runningCurrently: {equals: false}}, {cancelled: {equals: false}}]}  ]   }) {    cancelled    deleted    runningCurrently    trainNumber  }}");
        result3.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(5));

        final ResultActions result4 = this.query("{  trainsByDepartureDate(departureDate: \"2020-09-17\", where: {      and: [    {deleted: {equals: false}},     {or: [{runningCurrently: {equals: false}}, {cancelled: {equals: false}}]}  ]   }) {    cancelled    deleted    runningCurrently    trainNumber  }}");
        result4.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(3));
    }

    private void createTestData() {
        final Pair<Train, List<TimeTableRow>> train66 = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        final Pair<Train, List<TimeTableRow>> train67 = factoryService.getTrainFactory().createBaseTrain(new TrainId(67L, LocalDate.of(2020, 9, 17)));
        final Pair<Train, List<TimeTableRow>> train68 = factoryService.getTrainFactory().createBaseTrain(new TrainId(68L, LocalDate.of(2020, 9, 17)));
        final Pair<Train, List<TimeTableRow>> train69 = factoryService.getTrainFactory().createBaseTrain(new TrainId(69L, LocalDate.of(2020, 9, 17)));
        final Pair<Train, List<TimeTableRow>> train70 = factoryService.getTrainFactory().createBaseTrain(new TrainId(70L, LocalDate.of(2020, 9, 17)));
        final Pair<Train, List<TimeTableRow>> train71 = factoryService.getTrainFactory().createBaseTrain(new TrainId(71L, LocalDate.of(2020, 9, 17)));
        final Pair<Train, List<TimeTableRow>> train72 = factoryService.getTrainFactory().createBaseTrain(new TrainId(72L, LocalDate.of(2020, 9, 17)));
        final Pair<Train, List<TimeTableRow>> train73 = factoryService.getTrainFactory().createBaseTrain(new TrainId(73L, LocalDate.of(2020, 9, 17)));

        train66.getFirst().deleted = true;
        train66.getFirst().cancelled = true;
        train66.getFirst().runningCurrently = true;

        train67.getFirst().deleted = true;
        train67.getFirst().cancelled = true;
        train66.getFirst().runningCurrently = false;

        train68.getFirst().deleted = true;
        train68.getFirst().cancelled = false;
        train66.getFirst().runningCurrently = true;

        train69.getFirst().deleted = true;
        train69.getFirst().cancelled = false;
        train69.getFirst().runningCurrently = false;

        train70.getFirst().deleted = false;
        train70.getFirst().cancelled = true;
        train70.getFirst().runningCurrently = true;

        train71.getFirst().deleted = false;
        train71.getFirst().cancelled = true;
        train71.getFirst().runningCurrently = false;

        train72.getFirst().deleted = false;
        train72.getFirst().cancelled = false;
        train72.getFirst().runningCurrently = true;

        train73.getFirst().deleted = false;
        train73.getFirst().cancelled = false;
        train73.getFirst().runningCurrently = false;

        trainRepository.saveAll(List.of(train66.getFirst(), train67.getFirst(), train68.getFirst(), train69.getFirst(), train70.getFirst(), train71.getFirst(), train72.getFirst(), train73.getFirst()));
    }
}
