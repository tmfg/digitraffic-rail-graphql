package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import fi.digitraffic.graphql.rail.entities.TrainId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.factory.TrainFactory;


public class PrimitiveFilterQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

    private static final LocalDate DATE_2000_09_17 = LocalDate.of(2000, 9, 17);
    @Test
    public void dateTimeFilterShouldWork() throws Exception {
        final Train train66 = trainFactory.createBaseTrain(66, DATE_2000_09_17).getFirst();
        final Train train67 = trainFactory.createBaseTrain(67, DATE_2000_09_17).getFirst();
        final Train train68 = trainFactory.createBaseTrain(68, DATE_2000_09_17).getFirst();
        final Train train69 = trainFactory.createBaseTrain(69, DATE_2000_09_17).getFirst();

        final ZonedDateTime baseTime = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("Europe/Helsinki"));
        train66.timetableAcceptanceDate = baseTime;
        train67.timetableAcceptanceDate = baseTime.plusDays(1);
        train68.timetableAcceptanceDate = baseTime.plusDays(2);
        train69.timetableAcceptanceDate = baseTime.plusDays(3);

        trainRepository.saveAll(List.of(train66, train67, train68, train69));

        final ResultActions result = this.query(String.format("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where: { timetableAcceptanceDate:{equals:\"%s\"}}) {   trainNumber, timetableAcceptanceDate  }}", train66.timetableAcceptanceDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        final ResultActions result2 = this.query(String.format("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where: { timetableAcceptanceDate:{greaterThan:\"%s\"}}) {   trainNumber, timetableAcceptanceDate  }}", train67.timetableAcceptanceDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));

        final ResultActions result3 = this.query(String.format("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where: { timetableAcceptanceDate:{lessThan:\"%s\"}}) {   trainNumber, timetableAcceptanceDate  }}", train69.timetableAcceptanceDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        result3.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(3));
    }

    @Test
    public void integerFilterShouldWork() throws Exception {
        trainFactory.createBaseTrain(66, DATE_2000_09_17);
        trainFactory.createBaseTrain(67, DATE_2000_09_17);
        trainFactory.createBaseTrain(68, DATE_2000_09_17);
        trainFactory.createBaseTrain(69, DATE_2000_09_17);

        final ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where:{trainNumber:{equals:68}}) {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        final ResultActions result2 = this.query("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where:{trainNumber:{greaterThan:67}}) {   trainNumber, version  }}");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));

        final ResultActions result3 = this.query("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where:{trainNumber:{lessThan:69}}) {   trainNumber, version  }}");
        result3.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(3));
    }

    @Test
    public void enumFilterShouldWork() throws Exception {
        final Train train66 = trainFactory.createBaseTrain(66, DATE_2000_09_17).getFirst();
        final Train train67 = trainFactory.createBaseTrain(67, DATE_2000_09_17).getFirst();
        final Train train68 = trainFactory.createBaseTrain(68, DATE_2000_09_17).getFirst();
        final Train train69 = trainFactory.createBaseTrain(69, DATE_2000_09_17).getFirst();

        train67.timetableType = Train.TimetableType.ADHOC;
        trainRepository.save(train67);

        final ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where:{timetableType:{equals:\"ADHOC\"}}) {   trainNumber, version, timetableType  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        final ResultActions result2 = this.query("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where:{timetableType:{equals:\"REGULAR\"}}) {   trainNumber, version, timetableType  }}");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(3));

        final ResultActions result3 = this.query("{  trainsByDepartureDate(departureDate: \"2000-09-17\", where: { timeTableRows:{contains:{station:{type:{equals:\"TURNOUT_IN_THE_OPEN_LINE\"}}}}}) {    cancelled  }}");
        result3.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(0));

        final ResultActions result4 = this.query("{  trainsByDepartureDate(departureDate: \"2000-09-17\", where: { timeTableRows:{contains:{station:{type:{equals:\"STATION\"}}}}}) {    cancelled  }}");
        result4.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(4));
    }

    @Test
    public void stringFilterShouldWork() throws Exception {
        final Train train66 = trainFactory.createBaseTrain(66, DATE_2000_09_17).getFirst();
        final Train train67 = trainFactory.createBaseTrain(67, DATE_2000_09_17).getFirst();
        final Train train68 = trainFactory.createBaseTrain(68, DATE_2000_09_17).getFirst();
        final Train train69 = trainFactory.createBaseTrain(69, DATE_2000_09_17).getFirst();

        train66.commuterLineid = "A";
        trainRepository.save(train66);

        train67.commuterLineid = "B";
        trainRepository.save(train67);

        train68.commuterLineid = null;
        trainRepository.save(train68);

        train69.commuterLineid = null;
        trainRepository.save(train69);

        final ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where:{commuterLineid:{equals:\"A\"}}) {   trainNumber, version, commuterLineid  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        final ResultActions result2 = this.query("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where:{commuterLineid:{equals:\"B\"}}) {   trainNumber, version, commuterLineid  }}");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        final ResultActions result3 = this.query("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where:{commuterLineid:{equals:null}}) {   trainNumber, version, commuterLineid  }}");
        result3.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));
    }

    @Test
    public void booleanFilterShouldWork() throws Exception {
        final Train train66 = trainFactory.createBaseTrain(66, DATE_2000_09_17).getFirst();
        final Train train67 = trainFactory.createBaseTrain(67, DATE_2000_09_17).getFirst();
        final Train train68 = trainFactory.createBaseTrain(68, DATE_2000_09_17).getFirst();
        final Train train69 = trainFactory.createBaseTrain(69, DATE_2000_09_17).getFirst();

        train66.deleted = true;
        trainRepository.save(train66);

        train67.deleted = true;
        trainRepository.save(train67);

        train68.deleted = false;
        trainRepository.save(train68);

        train69.deleted = null;
        trainRepository.save(train69);

        final ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where:{deleted:{equals:true}}) {   trainNumber, version, deleted  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));

        final ResultActions result2 = this.query("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where:{deleted:{equals:false}}) {   trainNumber, version, deleted  }}");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
    }

    @Test
    public void unequalQueryShouldWork() throws Exception {
        trainFactory.createBaseTrain(66, DATE_2000_09_17);
        trainFactory.createBaseTrain(67, DATE_2000_09_17);

        final ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \"2000-09-17\", where: { trainNumber: {unequals: 66 } }) {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainNumber").value(67));
    }

    @Test
    public void timetableRowFiltering() throws Exception {
        trainFactory.createBaseTrain(new TrainId(67, DATE_2000_09_17), ZonedDateTime.of(2000, 9, 17, 2, 0, 0, 0, ZoneId.of("UTC")));
        trainFactory.createBaseTrain(new TrainId(67, DATE_2000_09_17), ZonedDateTime.of(2000, 9, 17, 12, 0, 0, 0, ZoneId.of("UTC")));

        final ResultActions result = this.query("""
                        { trainsByDepartureDate(departureDate: "2000-09-17", 
                            where: { 
                                timeTableRows: {
                                    contains: {
                                      and: [
                                        { scheduledTime: { greaterThan: "2000-09-17T10:00:00+02:00" }}
                                      ]
                                    }
                                  }
                            }) {   trainNumber, version  }}
                """);
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainNumber").value(67));
    }
}
