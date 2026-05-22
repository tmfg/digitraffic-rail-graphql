package fi.digitraffic.graphql.rail.to;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessageTypeEnum;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTypeTO;

class TrainTrackingTOConverterTest {

    private final TrainTrackingTOConverter converter = new TrainTrackingTOConverter();

    @Test
    void allScalarFieldsAreMappedFromProjectionRow() {
        // given
        final Object[] row = new Object[]{
                1L,                                                                                    // id
                "66",                                                                                  // trainNumber
                LocalDate.of(2024, 1, 1),                                                              // virtualDepartureDate
                "HKI",                                                                                 // stationShortCode
                "TPE",                                                                                 // nextStationShortCode
                "PSL",                                                                                 // previousStationShortCode
                42L,                                                                                   // version
                ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Helsinki")),               // timestamp
                "TRACK_001",                                                                           // track_section
                "TRACK_002",                                                                           // nextTrackSectionCode
                "TRACK_000",                                                                           // previousTrackSectionCode
                TrainTrackingMessageTypeEnum.OCCUPY                                                    // type
        };

        // when
        final TrainTrackingMessageTO to = converter.convertProjection(row);

        // then
        assertEquals(1, to.getId());
        assertEquals("66", to.getTrainNumber());
        assertEquals(LocalDate.of(2024, 1, 1), to.getDepartureDate());
        assertEquals("HKI", to.getStationShortCode());
        assertEquals("TPE", to.getNextStationShortCode());
        assertEquals("PSL", to.getPreviousStationShortCode());
        assertEquals("42", to.getVersion());
        assertEquals(ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Helsinki")), to.getTimestamp());
        assertEquals("TRACK_001", to.getTrackSectionCode());
        assertEquals("TRACK_002", to.getNextTrackSectionCode());
        assertEquals("TRACK_000", to.getPreviousTrackSectionCode());
        assertEquals(TrainTrackingMessageTypeTO.OCCUPY, to.getType());
        // Association fields must be null (resolved by separate link classes)
        assertNull(to.getStation());
        assertNull(to.getNextStation());
        assertNull(to.getPreviousStation());
        assertNull(to.getTrain());
        assertNull(to.getTrackSection());
    }

    @Test
    void nullableFieldsAcceptNull() {
        // given
        final Object[] row = new Object[]{
                1L, "66", LocalDate.of(2024, 1, 1),
                "HKI",
                null,   // nextStationShortCode
                null,   // previousStationShortCode
                1L,
                ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Helsinki")),
                "TRACK_001",
                null,   // nextTrackSectionCode
                null,   // previousTrackSectionCode
                TrainTrackingMessageTypeEnum.OCCUPY
        };

        // when
        final TrainTrackingMessageTO to = converter.convertProjection(row);

        // then
        assertNull(to.getNextStationShortCode());
        assertNull(to.getPreviousStationShortCode());
        assertNull(to.getNextTrackSectionCode());
        assertNull(to.getPreviousTrackSectionCode());
    }

    @Test
    void projectionOutputMatchesEntityConversion() {
        // given
        final var timestamp = ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZoneId.of("Europe/Helsinki"));

        final var entity = new TrainTrackingMessage();
        entity.id = 99L;
        entity.trainId = new StringVirtualDepartureDateTrainId("123", LocalDate.of(2024, 6, 15));
        entity.stationShortCode = "TKL";
        entity.nextStationShortCode = "KVL";
        entity.previousStationShortCode = "HKI";
        entity.version = 7L;
        entity.timestamp = timestamp;
        entity.track_section = "SECTION_A";
        entity.nextTrackSectionCode = "SECTION_B";
        entity.previousTrackSectionCode = "SECTION_Z";
        entity.type = TrainTrackingMessageTypeEnum.RELEASE;

        final Object[] row = new Object[]{
                99L, "123", LocalDate.of(2024, 6, 15),
                "TKL", "KVL", "HKI",
                7L, timestamp,
                "SECTION_A", "SECTION_B", "SECTION_Z",
                TrainTrackingMessageTypeEnum.RELEASE
        };

        // when
        final TrainTrackingMessageTO fromEntity = converter.convertEntity(entity);
        final TrainTrackingMessageTO fromProjection = converter.convertProjection(row);

        // then
        assertEquals(fromEntity.getId(), fromProjection.getId());
        assertEquals(fromEntity.getTrainNumber(), fromProjection.getTrainNumber());
        assertEquals(fromEntity.getDepartureDate(), fromProjection.getDepartureDate());
        assertEquals(fromEntity.getStationShortCode(), fromProjection.getStationShortCode());
        assertEquals(fromEntity.getNextStationShortCode(), fromProjection.getNextStationShortCode());
        assertEquals(fromEntity.getPreviousStationShortCode(), fromProjection.getPreviousStationShortCode());
        assertEquals(fromEntity.getVersion(), fromProjection.getVersion());
        assertEquals(fromEntity.getTimestamp(), fromProjection.getTimestamp());
        assertEquals(fromEntity.getTrackSectionCode(), fromProjection.getTrackSectionCode());
        assertEquals(fromEntity.getNextTrackSectionCode(), fromProjection.getNextTrackSectionCode());
        assertEquals(fromEntity.getPreviousTrackSectionCode(), fromProjection.getPreviousTrackSectionCode());
        assertEquals(fromEntity.getType(), fromProjection.getType());
    }

    @Test
    void releaseTypeIsCorrectlyMapped() {
        // given
        final Object[] row = new Object[]{
                1L, "1", LocalDate.of(2024, 1, 1),
                "HKI", null, null,
                1L, ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Helsinki")),
                "T1", null, null,
                TrainTrackingMessageTypeEnum.RELEASE
        };

        // when
        final TrainTrackingMessageTO to = converter.convertProjection(row);

        // then
        assertEquals(TrainTrackingMessageTypeTO.RELEASE, to.getType());
    }
}
