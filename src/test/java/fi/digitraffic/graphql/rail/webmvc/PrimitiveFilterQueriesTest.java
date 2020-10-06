package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;


public class PrimitiveFilterQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

    @Test
    public void dateTimeFilterShouldWork() throws Exception {
        Train train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1))).getLeft();
        Train train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1))).getLeft();
        Train train68 = trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2000, 1, 1))).getLeft();
        Train train69 = trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2000, 1, 1))).getLeft();

        ZonedDateTime baseTime = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.of("Europe/Helsinki"));
        train66.timetableAcceptanceDate = baseTime;
        train67.timetableAcceptanceDate = baseTime.plusDays(1);
        train68.timetableAcceptanceDate = baseTime.plusDays(2);
        train69.timetableAcceptanceDate = baseTime.plusDays(3);

        trainRepository.saveAll(List.of(train66, train67, train68, train69));

        ResultActions result = this.query(String.format("{ trainsByDepartureDate(departureDate: \\\"2000-01-01\\\", where: { timetableAcceptanceDate:{equals:\\\"%s\\\"}}) {   trainNumber, timetableAcceptanceDate  }}", train66.timetableAcceptanceDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        ResultActions result2 = this.query(String.format("{ trainsByDepartureDate(departureDate: \\\"2000-01-01\\\", where: { timetableAcceptanceDate:{greaterThan:\\\"%s\\\"}}) {   trainNumber, timetableAcceptanceDate  }}", train67.timetableAcceptanceDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));

        ResultActions result3 = this.query(String.format("{ trainsByDepartureDate(departureDate: \\\"2000-01-01\\\", where: { timetableAcceptanceDate:{lessThan:\\\"%s\\\"}}) {   trainNumber, timetableAcceptanceDate  }}", train69.timetableAcceptanceDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        result3.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(3));
    }

    @Test
    public void integerFilterShouldWork() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2000, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2000, 1, 1)));

        ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \\\"2000-01-01\\\", where:{trainNumber:{equals:68}}) {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        ResultActions result2 = this.query("{ trainsByDepartureDate(departureDate: \\\"2000-01-01\\\", where:{trainNumber:{greaterThan:67}}) {   trainNumber, version  }}");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));

        ResultActions result3 = this.query("{ trainsByDepartureDate(departureDate: \\\"2000-01-01\\\", where:{trainNumber:{lessThan:69}}) {   trainNumber, version  }}");
        result3.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(3));
    }

    @Test
    public void enumFilterShouldWork() throws Exception {
        Train train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17))).getLeft();
        Train train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2020, 9, 17))).getLeft();
        Train train68 = trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2020, 9, 17))).getLeft();
        Train train69 = trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2020, 9, 17))).getLeft();

        train67.timetableType = Train.TimetableType.ADHOC;
        trainRepository.save(train67);

        ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where:{timetableType:{equals:\\\"ADHOC\\\"}}) {   trainNumber, version, timetableType  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        ResultActions result2 = this.query("{ trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where:{timetableType:{equals:\\\"REGULAR\\\"}}) {   trainNumber, version, timetableType  }}");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(3));
    }

    @Test
    public void stringFilterShouldWork() throws Exception {
        Train train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17))).getLeft();
        Train train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2020, 9, 17))).getLeft();
        Train train68 = trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2020, 9, 17))).getLeft();
        Train train69 = trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2020, 9, 17))).getLeft();

        train66.commuterLineid = "A";
        trainRepository.save(train66);

        train67.commuterLineid = "B";
        trainRepository.save(train67);

        train68.commuterLineid = null;
        trainRepository.save(train68);

        train69.commuterLineid = null;
        trainRepository.save(train69);

        ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where:{commuterLineid:{equals:\\\"A\\\"}}) {   trainNumber, version, commuterLineid  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        ResultActions result2 = this.query("{ trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where:{commuterLineid:{equals:\\\"B\\\"}}) {   trainNumber, version, commuterLineid  }}");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        ResultActions result3 = this.query("{ trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where:{commuterLineid:{equals:null}}) {   trainNumber, version, commuterLineid  }}");
        result3.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));
    }

    @Test
    public void booleanFilterShouldWork() throws Exception {
        Train train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17))).getLeft();
        Train train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2020, 9, 17))).getLeft();
        Train train68 = trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2020, 9, 17))).getLeft();
        Train train69 = trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2020, 9, 17))).getLeft();

        train66.deleted = true;
        trainRepository.save(train66);

        train67.deleted = true;
        trainRepository.save(train67);

        train68.deleted = false;
        trainRepository.save(train68);

        train69.deleted = null;
        trainRepository.save(train69);

        ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where:{deleted:{equals:true}}) {   trainNumber, version, deleted  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));

        ResultActions result2 = this.query("{ trainsByDepartureDate(departureDate: \\\"2020-09-17\\\", where:{deleted:{equals:false}}) {   trainNumber, version, deleted  }}");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
    }

    @Test
    public void unequalQueryShouldWork() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1)));

        ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \\\"2000-01-01\\\", where: { trainNumber: {unequal: 66 } }) {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainNumber").value(67));
    }
}
