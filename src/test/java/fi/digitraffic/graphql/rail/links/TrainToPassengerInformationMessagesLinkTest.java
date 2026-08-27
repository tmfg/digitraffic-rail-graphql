package fi.digitraffic.graphql.rail.links;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;
import jakarta.persistence.EntityManagerFactory;

public class TrainToPassengerInformationMessagesLinkTest extends BaseWebMVCTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void linkShouldWork() throws Exception {

        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));

        factoryService.getPassengerInformationMessageFactory().create("1", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1);

        factoryService.getPassengerInformationMessageFactory().create("1", 2, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1);

        final ResultActions result = this.query("""
                {
                  trainsByDepartureDate(departureDate: "2024-01-01") {
                    trainNumber
                    passengerInformationMessages {
                      id
                      version
                    }
                  }
                }
                """);
        
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].passengerInformationMessages[0].version").value(2));
    }

    @Test
    void projectionDoesNotTriggerTrainQueriesViaNotFoundAnnotation() throws Exception {
        // given — create a train and PIM linked to it
        // PassengerInformationMessage has @NotFound(action = NotFoundAction.IGNORE) on its Train association,
        // which forces Hibernate to eagerly load Train for each message. Projection should bypass this.
        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));

        factoryService.getPassengerInformationMessageFactory().create("1", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1);
        factoryService.getPassengerInformationMessageFactory().create("2", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1);

        // Enable Hibernate statistics
        final Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);

        // Baseline: root train query WITHOUT passengerInformationMessages child link
        stats.clear();
        query("""
                {
                  trainsByDepartureDate(departureDate: "2024-01-01") {
                    trainNumber
                  }
                }
                """);
        final long baselineTrainLoads = stats.getEntityStatistics(
                "fi.digitraffic.graphql.rail.entities.Train").getLoadCount();

        // when — query only scalar fields (no train/messageStations/audio/video links)
        stats.clear();
        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2024-01-01") {
                    passengerInformationMessages {
                      id
                      version
                      creationDateTime
                      startValidity
                      endValidity
                    }
                  }
                }
                """);

        // then — Train loads should not increase beyond baseline (from root trainsByDepartureDate query)
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].passengerInformationMessages.length()").value(2));
        final long trainLoads = stats.getEntityStatistics(
                "fi.digitraffic.graphql.rail.entities.Train").getLoadCount();
        assertEquals(baselineTrainLoads, trainLoads,
                "Projection should not trigger additional Train entity loads via @NotFound, " +
                "but found " + trainLoads + " vs baseline " + baselineTrainLoads);

        stats.setStatisticsEnabled(false);
    }
}



