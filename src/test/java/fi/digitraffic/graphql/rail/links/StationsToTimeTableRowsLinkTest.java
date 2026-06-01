package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class StationsToTimeTableRowsLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void linkShouldWork() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, DATE);

        final ResultActions result = this.query("""
                {
                    stations {
                        shortCode
                        timeTableRows {
                            type
                            scheduledTime
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.stations[?(@.shortCode == 'HKI')].timeTableRows").isNotEmpty());
    }

    @Test
    public void whereOrderByAndTakeShouldWork() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, DATE);

        final ResultActions result = this.query("""
                {
                    stations(where: { shortCode: { equals: "TPE" } }) {
                        shortCode
                        timeTableRows(
                            where: { type: { equals: "ARRIVAL" } }
                            orderBy: [{ scheduledTime: DESCENDING }]
                            take: 1
                        ) {
                            type
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.stations.length()").value(1));
        result.andExpect(jsonPath("$.data.stations[0].shortCode").value("TPE"));
        result.andExpect(jsonPath("$.data.stations[0].timeTableRows.length()").value(1));
        result.andExpect(jsonPath("$.data.stations[0].timeTableRows[0].type").value("ARRIVAL"));
    }

    @Test
    public void skipShouldWork() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, DATE);

        final ResultActions result = this.query("""
                {
                    stations(where: { shortCode: { equals: "HKI" } }) {
                        shortCode
                        timeTableRows(skip: 99) {
                            type
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.stations.length()").value(1));
        result.andExpect(jsonPath("$.data.stations[0].timeTableRows").isEmpty());
    }
}

