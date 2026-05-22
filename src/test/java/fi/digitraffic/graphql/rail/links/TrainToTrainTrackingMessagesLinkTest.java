package fi.digitraffic.graphql.rail.links;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.repositories.TrainTrackingMessageRepository;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;
import jakarta.persistence.EntityManagerFactory;

class TrainToTrainTrackingMessagesLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2020, 9, 17);

    @Autowired
    private TrainTrackingMessageRepository trainTrackingMessageRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void linkReturnsTrackingMessagesForTrain() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, DATE));
        factoryService.getTrainTrackingMessageFactory().create(train.getFirst());

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    trainTrackingMessages {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[0].version").value("1"));
    }

    @Test
    void linkReturnsEmptyListWhenNoTrackingMessages() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, DATE));

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    trainTrackingMessages {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages.length()").value(0));
    }

    @Test
    void linkOnlyReturnsMessagesForCorrectTrain() throws Exception {
        final var train1 = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, DATE));
        final var train2 = factoryService.getTrainFactory().createBaseTrain(new TrainId(77L, DATE));
        factoryService.getTrainTrackingMessageFactory().create(train1.getFirst());

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    trainTrackingMessages {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==66)].trainTrackingMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==77)].trainTrackingMessages.length()").value(0));
    }

    // --- Projection regression tests (verify behavior is preserved after projection is implemented) ---

    @Test
    void allFieldsArePopulatedCorrectly() throws Exception {
        // given
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, DATE));
        factoryService.getTrainTrackingMessageFactory().create(train.getFirst());

        // when — query all non-hidden scalar fields
        // Hidden fields: trainNumber, departureDate, stationShortCode, nextStationShortCode, previousStationShortCode
        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    trainTrackingMessages {
                      id
                      version
                      timestamp
                      trackSectionCode
                      nextTrackSectionCode
                      previousTrackSectionCode
                      type
                    }
                  }
                }
                """);

        // then
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[0].version").value("1"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[0].trackSectionCode").value("TEST_UNIQUE"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[0].type").value("OCCUPY"));
    }

    @Test
    void whereOnScalarFieldFiltersCorrectly() throws Exception {
        // given
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, DATE));
        factoryService.getTrainTrackingMessageFactory().create(train.getFirst());
        final var message2 = factoryService.getTrainTrackingMessageFactory().create(train.getFirst());
        message2.version = 99L;
        trainTrackingMessageRepository.save(message2);

        // when
        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainTrackingMessages(where: {version: {equals: "99"}}) {
                      version
                    }
                  }
                }
                """);

        // then
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[0].version").value("99"));
    }

    @Test
    void orderByTimestampDescReversesDefaultOrder() throws Exception {
        // given
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, DATE));
        factoryService.getTrainTrackingMessageFactory().create(train.getFirst());
        final var message2 = factoryService.getTrainTrackingMessageFactory().create(train.getFirst());
        message2.version = 2L;
        message2.timestamp = message2.timestamp.plusHours(1);
        trainTrackingMessageRepository.save(message2);

        // when — default order is ASC by timestamp, so DESC should reverse
        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainTrackingMessages(orderBy: [{timestamp: DESCENDING}]) {
                      version
                    }
                  }
                }
                """);

        // then
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages.length()").value(2));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[0].version").value("2"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[1].version").value("1"));
    }

    @Test
    void whereOnAssociationPathFiltersCorrectly() throws Exception {
        // given
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, DATE));
        factoryService.getStationFactory().create("TEST99", 99, "FI");
        factoryService.getTrainTrackingMessageFactory().create(train.getFirst());

        // when
        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainTrackingMessages(where: {station: {shortCode: {equals: "TEST99"}}}) {
                      version
                    }
                  }
                }
                """);

        // then
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[0].version").value("1"));
    }

    @Test
    void childLinksResolveCorrectly() throws Exception {
        // given
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, DATE));
        factoryService.getStationFactory().create("TEST99", 99, "FI");
        factoryService.getTrackSectionFactory().create("TEST99", "TEST_UNIQUE");
        factoryService.getTrainTrackingMessageFactory().create(train.getFirst());

        // when
        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainTrackingMessages {
                      trackSectionCode
                      station { shortCode }
                      train { trainNumber }
                      trackSection { trackSectionCode }
                    }
                  }
                }
                """);

        // then
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[0].station.shortCode").value("TEST99"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[0].train.trainNumber").value(66));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[0].trackSection.trackSectionCode").value("TEST_UNIQUE"));
    }

    @Test
    void multipleTrainsBatchedCorrectly() throws Exception {
        // given
        final var train1 = factoryService.getTrainFactory().createBaseTrain(new TrainId(10L, DATE));
        final var train2 = factoryService.getTrainFactory().createBaseTrain(new TrainId(20L, DATE));
        final var train3 = factoryService.getTrainFactory().createBaseTrain(new TrainId(30L, DATE));
        factoryService.getTrainTrackingMessageFactory().create(train1.getFirst());
        factoryService.getTrainTrackingMessageFactory().create(train1.getFirst());
        factoryService.getTrainTrackingMessageFactory().create(train2.getFirst());
        // train3 has no messages

        // when
        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    trainTrackingMessages { version }
                  }
                }
                """);

        // then
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==10)].trainTrackingMessages.length()").value(2));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==20)].trainTrackingMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==30)].trainTrackingMessages.length()").value(0));
    }

    @Test
    void projectionDoesNotTriggerStationQueries() throws Exception {
        // given — create a train with multiple tracking messages, each with a stationShortCode
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, DATE));
        factoryService.getStationFactory().create("TEST99", 99, "FI");
        for (int i = 0; i < 5; i++) {
            factoryService.getTrainTrackingMessageFactory().create(train.getFirst());
        }

        // Enable Hibernate statistics to count entity loads
        final Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        // when — query only scalar fields (no station/train/trackSection links)
        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainTrackingMessages {
                      timestamp
                      trackSectionCode
                      type
                      version
                    }
                  }
                }
                """);

        // then — projection bypasses entity loading, so Station must never be loaded
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages.length()").value(5));
        final long stationLoads = stats.getEntityStatistics(
                "fi.digitraffic.graphql.rail.entities.Station").getLoadCount();
        assertEquals(0, stationLoads,
                "Projection should not trigger any Station entity loads, but found " + stationLoads);

        stats.setStatisticsEnabled(false);
    }
}

