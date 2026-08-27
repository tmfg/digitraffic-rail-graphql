package fi.digitraffic.graphql.rail.to;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.TimeTableRowId;
import fi.digitraffic.graphql.rail.model.EstimateSourceTypeTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTypeTO;
import jakarta.persistence.Tuple;

class TimeTableRowTOConverterTest {

    private final TimeTableRowTOConverter converter = new TimeTableRowTOConverter();

    @Test
    void allScalarFieldsAreMappedFromProjectionRow() {
        // given
        final Tuple row = TestTuple.builder()
                .put("attapId", 100L)
                .put("trainNumber", 66L)
                .put("departureDate", LocalDate.of(2024, 6, 1))
                .put("stationShortCode", "HKI")
                .put("stationUICCode", 42)
                .put("countryCode", "FI")
                .put("type", TimeTableRow.TimeTableRowType.DEPARTURE)
                .put("trainStopping", true)
                .put("commercialStop", true)
                .put("commercialTrack", "3")
                .put("cancelled", false)
                .put("scheduledTime", ZonedDateTime.of(2024, 6, 1, 10, 0, 0, 0, ZoneId.of("Europe/Helsinki")))
                .put("actualTime", ZonedDateTime.of(2024, 6, 1, 10, 2, 0, 0, ZoneId.of("Europe/Helsinki")))
                .put("differenceInMinutes", 5L)
                .put("liveEstimateTime", ZonedDateTime.of(2024, 6, 1, 10, 1, 0, 0, ZoneId.of("Europe/Helsinki")))
                .put("estimateSource", TimeTableRow.EstimateSourceEnum.COMBOCALC)
                .put("unknownDelay", false)
                .put("stopSector", "A")
                .build();

        // when
        final TimeTableRowTO to = converter.convertProjection(row);

        // then
        assertEquals("HKI", to.getStationShortCode());
        assertEquals(42, to.getStationUICCode());
        assertEquals("FI", to.getCountryCode());
        assertEquals(TimeTableRowTypeTO.DEPARTURE, to.getType());
        assertEquals(true, to.getTrainStopping());
        assertEquals(true, to.getCommercialStop());
        assertEquals("3", to.getCommercialTrack());
        assertEquals(false, to.getCancelled());
        assertEquals(ZonedDateTime.of(2024, 6, 1, 10, 0, 0, 0, ZoneId.of("Europe/Helsinki")), to.getScheduledTime());
        assertEquals(ZonedDateTime.of(2024, 6, 1, 10, 2, 0, 0, ZoneId.of("Europe/Helsinki")), to.getActualTime());
        assertEquals(5, to.getDifferenceInMinutes());
        assertEquals(ZonedDateTime.of(2024, 6, 1, 10, 1, 0, 0, ZoneId.of("Europe/Helsinki")), to.getLiveEstimateTime());
        assertEquals(EstimateSourceTypeTO.COMBOCALC, to.getEstimateSourceType());
        assertEquals(false, to.getUnknownDelay());
        assertEquals("A", to.getStopSector());
        assertEquals(100, to.getId());
        assertEquals(66, to.getTrainNumber());
        assertEquals(LocalDate.of(2024, 6, 1), to.getDepartureDate());
        // Association fields must be null (resolved by separate link classes)
        assertNull(to.getStation());
        assertNull(to.getTrain());
        assertNull(to.getCauses());
    }

    @Test
    void nullableFieldsAcceptNull() {
        // given
        final Tuple row = TestTuple.builder()
                .put("attapId", 100L)
                .put("trainNumber", 66L)
                .put("departureDate", LocalDate.of(2024, 6, 1))
                .put("stationShortCode", "HKI")
                .put("stationUICCode", 42)
                .put("countryCode", "FI")
                .put("type", TimeTableRow.TimeTableRowType.DEPARTURE)
                .put("trainStopping", true)
                .put("commercialStop", null)
                .put("commercialTrack", null)
                .put("cancelled", false)
                .put("scheduledTime", ZonedDateTime.of(2024, 6, 1, 10, 0, 0, 0, ZoneId.of("Europe/Helsinki")))
                .put("actualTime", null)
                .put("differenceInMinutes", null)
                .put("liveEstimateTime", null)
                .put("estimateSource", null)
                .put("unknownDelay", null)
                .put("stopSector", null)
                .build();

        // when
        final TimeTableRowTO to = converter.convertProjection(row);

        // then
        assertNull(to.getCommercialStop());
        assertNull(to.getCommercialTrack());
        assertNull(to.getActualTime());
        assertNull(to.getDifferenceInMinutes());
        assertNull(to.getLiveEstimateTime());
        assertNull(to.getEstimateSourceType());
        assertNull(to.getUnknownDelay());
        assertNull(to.getStopSector());
    }

    @Test
    void projectionOutputMatchesEntityConversion() {
        // given
        final var scheduledTime = ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZoneId.of("Europe/Helsinki"));
        final var actualTime = ZonedDateTime.of(2024, 6, 15, 12, 32, 0, 0, ZoneId.of("Europe/Helsinki"));
        final var liveEstimate = ZonedDateTime.of(2024, 6, 15, 12, 31, 0, 0, ZoneId.of("Europe/Helsinki"));

        final var entity = new TimeTableRow();
        entity.id = new TimeTableRowId(100L, LocalDate.of(2024, 6, 15), 66L);
        entity.stationShortCode = "TKL";
        entity.stationUICCode = 42;
        entity.countryCode = "FI";
        entity.type = TimeTableRow.TimeTableRowType.ARRIVAL;
        entity.trainStopping = true;
        entity.commercialStop = true;
        entity.commercialTrack = "2";
        entity.cancelled = false;
        entity.scheduledTime = scheduledTime;
        entity.actualTime = actualTime;
        entity.differenceInMinutes = 2L;
        entity.liveEstimateTime = liveEstimate;
        entity.estimateSource = TimeTableRow.EstimateSourceEnum.LIIKE_AUTOMATIC;
        entity.unknownDelay = false;
        entity.stopSector = "B";

        final Tuple row = TestTuple.builder()
                .put("attapId", 100L)
                .put("trainNumber", 66L)
                .put("departureDate", LocalDate.of(2024, 6, 15))
                .put("stationShortCode", "TKL")
                .put("stationUICCode", 42)
                .put("countryCode", "FI")
                .put("type", TimeTableRow.TimeTableRowType.ARRIVAL)
                .put("trainStopping", true)
                .put("commercialStop", true)
                .put("commercialTrack", "2")
                .put("cancelled", false)
                .put("scheduledTime", scheduledTime)
                .put("actualTime", actualTime)
                .put("differenceInMinutes", 2L)
                .put("liveEstimateTime", liveEstimate)
                .put("estimateSource", TimeTableRow.EstimateSourceEnum.LIIKE_AUTOMATIC)
                .put("unknownDelay", false)
                .put("stopSector", "B")
                .build();

        // when
        final TimeTableRowTO fromEntity = converter.convertEntity(entity);
        final TimeTableRowTO fromProjection = converter.convertProjection(row);

        // then
        assertEquals(fromEntity.getStationShortCode(), fromProjection.getStationShortCode());
        assertEquals(fromEntity.getStationUICCode(), fromProjection.getStationUICCode());
        assertEquals(fromEntity.getCountryCode(), fromProjection.getCountryCode());
        assertEquals(fromEntity.getType(), fromProjection.getType());
        assertEquals(fromEntity.getTrainStopping(), fromProjection.getTrainStopping());
        assertEquals(fromEntity.getCommercialStop(), fromProjection.getCommercialStop());
        assertEquals(fromEntity.getCommercialTrack(), fromProjection.getCommercialTrack());
        assertEquals(fromEntity.getCancelled(), fromProjection.getCancelled());
        assertEquals(fromEntity.getScheduledTime(), fromProjection.getScheduledTime());
        assertEquals(fromEntity.getActualTime(), fromProjection.getActualTime());
        assertEquals(fromEntity.getDifferenceInMinutes(), fromProjection.getDifferenceInMinutes());
        assertEquals(fromEntity.getLiveEstimateTime(), fromProjection.getLiveEstimateTime());
        assertEquals(fromEntity.getEstimateSourceType(), fromProjection.getEstimateSourceType());
        assertEquals(fromEntity.getUnknownDelay(), fromProjection.getUnknownDelay());
        assertEquals(fromEntity.getStopSector(), fromProjection.getStopSector());
        assertEquals(fromEntity.getId(), fromProjection.getId());
        assertEquals(fromEntity.getTrainNumber(), fromProjection.getTrainNumber());
        assertEquals(fromEntity.getDepartureDate(), fromProjection.getDepartureDate());
    }

    @Test
    void bothTimeTableRowTypesAreCorrectlyMapped() {
        // given
        final Tuple arrivalRow = createMinimalRow(TimeTableRow.TimeTableRowType.ARRIVAL);
        final Tuple departureRow = createMinimalRow(TimeTableRow.TimeTableRowType.DEPARTURE);

        // when
        final TimeTableRowTO arrivalTo = converter.convertProjection(arrivalRow);
        final TimeTableRowTO departureTo = converter.convertProjection(departureRow);

        // then
        assertEquals(TimeTableRowTypeTO.ARRIVAL, arrivalTo.getType());
        assertEquals(TimeTableRowTypeTO.DEPARTURE, departureTo.getType());
    }

    private Tuple createMinimalRow(final TimeTableRow.TimeTableRowType type) {
        return TestTuple.builder()
                .put("attapId", 1L)
                .put("trainNumber", 1L)
                .put("departureDate", LocalDate.of(2024, 1, 1))
                .put("stationShortCode", "HKI")
                .put("stationUICCode", 1)
                .put("countryCode", "FI")
                .put("type", type)
                .put("trainStopping", true)
                .put("commercialStop", null)
                .put("commercialTrack", null)
                .put("cancelled", false)
                .put("scheduledTime", ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Helsinki")))
                .put("actualTime", null)
                .put("differenceInMinutes", null)
                .put("liveEstimateTime", null)
                .put("estimateSource", null)
                .put("unknownDelay", null)
                .put("stopSector", null)
                .build();
    }
}
