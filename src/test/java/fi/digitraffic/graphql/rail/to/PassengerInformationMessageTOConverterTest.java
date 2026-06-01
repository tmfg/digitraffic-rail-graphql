package fi.digitraffic.graphql.rail.to;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageId;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import jakarta.persistence.Tuple;

class PassengerInformationMessageTOConverterTest {

    private final PassengerInformationMessageTOConverter converter = new PassengerInformationMessageTOConverter();

    @Test
    void allScalarFieldsAreMappedFromProjectionRow() {
        // given
        final var creationDateTime = ZonedDateTime.of(2024, 6, 1, 8, 0, 0, 0, ZoneId.of("Europe/Helsinki"));
        final var startValidity = ZonedDateTime.of(2024, 6, 1, 6, 0, 0, 0, ZoneId.of("Europe/Helsinki"));
        final var endValidity = ZonedDateTime.of(2024, 6, 2, 6, 0, 0, 0, ZoneId.of("Europe/Helsinki"));

        final Tuple row = TestTuple.builder()
                .put("id", "MSG001")
                .put("version", 3)
                .put("creationDateTime", creationDateTime)
                .put("startValidity", startValidity)
                .put("endValidity", endValidity)
                .put("trainDepartureDate", LocalDate.of(2024, 6, 1))
                .put("trainNumber", 42L)
                .build();

        // when
        final PassengerInformationMessageTO to = converter.convertProjection(row);

        // then
        assertEquals("MSG001", to.getId());
        assertEquals(3, to.getVersion());
        assertEquals(creationDateTime, to.getCreationDateTime());
        assertEquals(startValidity, to.getStartValidity());
        assertEquals(endValidity, to.getEndValidity());
        assertEquals(LocalDate.of(2024, 6, 1), to.getTrainDepartureDate());
        assertEquals(42, to.getTrainNumber());
        // Association fields must be null
        assertNull(to.getTrain());
        assertNull(to.getMessageStations());
        assertNull(to.getAudio());
        assertNull(to.getVideo());
    }

    @Test
    void nullableFieldsAcceptNull() {
        // given
        final var creationDateTime = ZonedDateTime.of(2024, 6, 1, 8, 0, 0, 0, ZoneId.of("Europe/Helsinki"));
        final var startValidity = ZonedDateTime.of(2024, 6, 1, 6, 0, 0, 0, ZoneId.of("Europe/Helsinki"));
        final var endValidity = ZonedDateTime.of(2024, 6, 2, 6, 0, 0, 0, ZoneId.of("Europe/Helsinki"));

        final Tuple row = TestTuple.builder()
                .put("id", "MSG001")
                .put("version", 1)
                .put("creationDateTime", creationDateTime)
                .put("startValidity", startValidity)
                .put("endValidity", endValidity)
                .put("trainDepartureDate", null)
                .put("trainNumber", null)
                .build();

        // when
        final PassengerInformationMessageTO to = converter.convertProjection(row);

        // then
        assertNull(to.getTrainDepartureDate());
        assertNull(to.getTrainNumber());
    }

    @Test
    void projectionOutputMatchesEntityConversion() {
        // given
        final var creationDateTime = ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneId.of("Europe/Helsinki"));
        final var startValidity = ZonedDateTime.of(2024, 6, 14, 0, 0, 0, 0, ZoneId.of("Europe/Helsinki"));
        final var endValidity = ZonedDateTime.of(2024, 6, 16, 0, 0, 0, 0, ZoneId.of("Europe/Helsinki"));

        final var entity = new PassengerInformationMessage();
        entity.id = new PassengerInformationMessageId("MSG002", 5);
        entity.creationDateTime = creationDateTime;
        entity.startValidity = startValidity;
        entity.endValidity = endValidity;
        entity.trainDepartureDate = LocalDate.of(2024, 6, 15);
        entity.trainNumber = 66L;

        final Tuple row = TestTuple.builder()
                .put("id", "MSG002")
                .put("version", 5)
                .put("creationDateTime", creationDateTime)
                .put("startValidity", startValidity)
                .put("endValidity", endValidity)
                .put("trainDepartureDate", LocalDate.of(2024, 6, 15))
                .put("trainNumber", 66L)
                .build();

        // when
        final PassengerInformationMessageTO fromEntity = converter.convertEntity(entity);
        final PassengerInformationMessageTO fromProjection = converter.convertProjection(row);

        // then
        assertEquals(fromEntity.getId(), fromProjection.getId());
        assertEquals(fromEntity.getVersion(), fromProjection.getVersion());
        assertEquals(fromEntity.getCreationDateTime(), fromProjection.getCreationDateTime());
        assertEquals(fromEntity.getStartValidity(), fromProjection.getStartValidity());
        assertEquals(fromEntity.getEndValidity(), fromProjection.getEndValidity());
        assertEquals(fromEntity.getTrainDepartureDate(), fromProjection.getTrainDepartureDate());
        assertEquals(fromEntity.getTrainNumber(), fromProjection.getTrainNumber());
    }
}
