package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class JourneySectionToTimeTableRowLinksTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void startAndEndTimeTableRowLinksShouldWork() throws Exception {
        final var pair = factoryService.getTrainFactory().createBaseTrain(1, DATE);
        final var train = pair.getFirst();
        final var rows = pair.getSecond();
        // attapId = row.id.attapId, saapAttapId = next row's attapId
        final long startAttapId = rows.get(0).id.attapId;
        final long endAttapId = rows.get(1).id.attapId;

        factoryService.getCompositionFactory().create(train);
        factoryService.getJourneySectionFactory().create(train, 120, 500, startAttapId, endAttapId);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        journeySections {
                            startTimeTableRow {
                                type
                                scheduledTime
                            }
                            endTimeTableRow {
                                type
                                scheduledTime
                            }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].startTimeTableRow.type").value("DEPARTURE"));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].endTimeTableRow").exists());
    }
}

