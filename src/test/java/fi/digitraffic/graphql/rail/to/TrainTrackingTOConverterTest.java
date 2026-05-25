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
import jakarta.persistence.Tuple;

class TrainTrackingTOConverterTest {

    private final TrainTrackingTOConverter converter = new TrainTrackingTOConverter();

    @Test
    void allScalarFieldsAreMappedFromProjectionRow() {
        // given
        final Tuple row = TestTuple.builder()
                .put("id", 1L)
                .put("trainNumber", "66")
                .put("virtualDepartureDate", LocalDate.of(2024, 1, 1))
                .put("stationShortCode", "HKI")
                .put("nextStationShortCode", "TPE")
                .put("previousStationShortCode", "PSL")
                .put("version", 42L)
                .put("timestamp", ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Helsinki")))
                .put("track_section", "TRACK_001")
                .put("nextTrackSectionCode", "TRACK_002")
                .put("previousTrackSectionCode", "TRACK_000")
                .put("type", TrainTrackingMessageTypeEnum.OCCUPY)
                .build();

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
        final Tuple row = TestTuple.builder()
                .put("id", 1L)
                .put("trainNumber", "66")
                .put("virtualDepartureDate", LocalDate.of(2024, 1, 1))
                .put("stationShortCode", "HKI")
                .put("nextStationShortCode", null)
                .put("previousStationShortCode", null)
                .put("version", 1L)
                .put("timestamp", ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Helsinki")))
                .put("track_section", "TRACK_001")
                .put("nextTrackSectionCode", null)
                .put("previousTrackSectionCode", null)
                .put("type", TrainTrackingMessageTypeEnum.OCCUPY)
                .build();

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

        final Tuple row = TestTuple.builder()
                .put("id", 99L)
                .put("trainNumber", "123")
                .put("virtualDepartureDate", LocalDate.of(2024, 6, 15))
                .put("stationShortCode", "TKL")
                .put("nextStationShortCode", "KVL")
                .put("previousStationShortCode", "HKI")
                .put("version", 7L)
                .put("timestamp", timestamp)
                .put("track_section", "SECTION_A")
                .put("nextTrackSectionCode", "SECTION_B")
                .put("previousTrackSectionCode", "SECTION_Z")
                .put("type", TrainTrackingMessageTypeEnum.RELEASE)
                .build();

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
        final Tuple row = TestTuple.builder()
                .put("id", 1L)
                .put("trainNumber", "1")
                .put("virtualDepartureDate", LocalDate.of(2024, 1, 1))
                .put("stationShortCode", "HKI")
                .put("nextStationShortCode", null)
                .put("previousStationShortCode", null)
                .put("version", 1L)
                .put("timestamp", ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Helsinki")))
                .put("track_section", "T1")
                .put("nextTrackSectionCode", null)
                .put("previousTrackSectionCode", null)
                .put("type", TrainTrackingMessageTypeEnum.RELEASE)
                .build();

        // when
        final TrainTrackingMessageTO to = converter.convertProjection(row);

        // then
        assertEquals(TrainTrackingMessageTypeTO.RELEASE, to.getType());
    }
}
