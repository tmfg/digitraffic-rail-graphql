package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;


public class TrainTypeToTrainCategoryLinkTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

    @Test
    public void linkShouldWork() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));

        final ResultActions result = this.query("{  trainsByDepartureDate(departureDate: \"2020-09-17\") {   trainNumber trainType {name trainCategory {name}}  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
    }
}
