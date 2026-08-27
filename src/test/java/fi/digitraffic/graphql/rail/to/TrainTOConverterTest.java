package fi.digitraffic.graphql.rail.to;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.model.TimetableTypeTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import jakarta.persistence.Tuple;

class TrainTOConverterTest {

    private final TrainTOConverter converter = new TrainTOConverter();

    @Test
    void allScalarFieldsAreMappedFromProjectionRow() {
        // given
        final Tuple row = TestTuple.builder()
                .put("cancelled", false)
                .put("commuterLineid", "Z")
                .put("deleted", null)
                .put("departureDate", LocalDate.of(2024, 6, 1))
                .put("operatorShortCode", "test")
                .put("runningCurrently", true)
                .put("timetableAcceptanceDate", ZonedDateTime.of(2024, 6, 1, 8, 0, 0, 0, ZoneId.of("Europe/Helsinki")))
                .put("timetableType", Train.TimetableType.REGULAR)
                .put("trainNumber", 66L)
                .put("version", 42L)
                .put("trainTypeId", 1L)
                .put("trainCategoryId", 2L)
                .build();

        // when
        final TrainTO to = converter.convertProjection(row);

        // then
        assertEquals(false, to.getCancelled());
        assertEquals("Z", to.getCommuterLineid());
        assertNull(to.getDeleted());
        assertEquals(LocalDate.of(2024, 6, 1), to.getDepartureDate());
        assertEquals("test", to.getOperatorShortCode());
        assertEquals(true, to.getRunningCurrently());
        assertEquals(ZonedDateTime.of(2024, 6, 1, 8, 0, 0, 0, ZoneId.of("Europe/Helsinki")), to.getTimetableAcceptanceDate());
        assertEquals(TimetableTypeTO.REGULAR, to.getTimetableType());
        assertEquals(66, to.getTrainNumber());
        assertEquals("42", to.getVersion());
        assertEquals(1, to.getTrainTypeId());
        assertEquals(2, to.getTrainCategoryId());
        // Association fields must be null
        assertNull(to.getOperator());
        assertNull(to.getTrainType());
        assertNull(to.getTimeTableRows());
        assertNull(to.getTrainLocations());
        assertNull(to.getCompositions());
        assertNull(to.getTrainTrackingMessages());
        assertNull(to.getRoutesetMessages());
        assertNull(to.getPassengerInformationMessages());
    }

    @Test
    void nullableFieldsAcceptNull() {
        // given
        final Tuple row = TestTuple.builder()
                .put("cancelled", false)
                .put("commuterLineid", null)
                .put("deleted", null)
                .put("departureDate", LocalDate.of(2024, 6, 1))
                .put("operatorShortCode", "test")
                .put("runningCurrently", false)
                .put("timetableAcceptanceDate", ZonedDateTime.of(2024, 6, 1, 8, 0, 0, 0, ZoneId.of("Europe/Helsinki")))
                .put("timetableType", Train.TimetableType.ADHOC)
                .put("trainNumber", 1L)
                .put("version", null)
                .put("trainTypeId", 1L)
                .put("trainCategoryId", 1L)
                .build();

        // when
        final TrainTO to = converter.convertProjection(row);

        // then
        assertNull(to.getCommuterLineid());
        assertNull(to.getDeleted());
        assertNull(to.getVersion());
    }

    @Test
    void projectionOutputMatchesEntityConversion() {
        // given
        final var acceptanceDate = ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneId.of("Europe/Helsinki"));

        final var entity = new Train();
        entity.id = new TrainId(66L, LocalDate.of(2024, 6, 15));
        entity.cancelled = false;
        entity.commuterLineid = "Z";
        entity.deleted = null;
        entity.operatorShortCode = "vr";
        entity.operatorUicCode = 10;
        entity.runningCurrently = true;
        entity.timetableAcceptanceDate = acceptanceDate;
        entity.timetableType = Train.TimetableType.REGULAR;
        entity.version = 7L;
        entity.trainTypeId = 1L;
        entity.trainCategoryId = 2L;

        final Tuple row = TestTuple.builder()
                .put("cancelled", false)
                .put("commuterLineid", "Z")
                .put("deleted", null)
                .put("departureDate", LocalDate.of(2024, 6, 15))
                .put("operatorShortCode", "vr")
                .put("runningCurrently", true)
                .put("timetableAcceptanceDate", acceptanceDate)
                .put("timetableType", Train.TimetableType.REGULAR)
                .put("trainNumber", 66L)
                .put("version", 7L)
                .put("trainTypeId", 1L)
                .put("trainCategoryId", 2L)
                .build();

        // when
        final TrainTO fromEntity = converter.convertEntity(entity);
        final TrainTO fromProjection = converter.convertProjection(row);

        // then
        assertEquals(fromEntity.getCancelled(), fromProjection.getCancelled());
        assertEquals(fromEntity.getCommuterLineid(), fromProjection.getCommuterLineid());
        assertEquals(fromEntity.getDeleted(), fromProjection.getDeleted());
        assertEquals(fromEntity.getDepartureDate(), fromProjection.getDepartureDate());
        assertEquals(fromEntity.getOperatorShortCode(), fromProjection.getOperatorShortCode());
        assertEquals(fromEntity.getRunningCurrently(), fromProjection.getRunningCurrently());
        assertEquals(fromEntity.getTimetableAcceptanceDate(), fromProjection.getTimetableAcceptanceDate());
        assertEquals(fromEntity.getTimetableType(), fromProjection.getTimetableType());
        assertEquals(fromEntity.getTrainNumber(), fromProjection.getTrainNumber());
        assertEquals(fromEntity.getVersion(), fromProjection.getVersion());
        assertEquals(fromEntity.getTrainTypeId(), fromProjection.getTrainTypeId());
        assertEquals(fromEntity.getTrainCategoryId(), fromProjection.getTrainCategoryId());
    }
}
