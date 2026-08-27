package fi.digitraffic.graphql.rail.to;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fi.digitraffic.graphql.rail.entities.Routesection;
import fi.digitraffic.graphql.rail.model.RoutesectionTO;
import jakarta.persistence.Tuple;

class RoutesectionTOConverterTest {

    private final RoutesectionTOConverter converter = new RoutesectionTOConverter();

    @Test
    void allScalarFieldsAreMappedFromProjectionRow() {
        // given
        final Tuple row = TestTuple.builder()
                .put("sectionId", "SEC001")
                .put("commercialTrackId", "TRACK_A")
                .put("stationCode", "HKI")
                .put("routesetId", 42L)
                .put("sectionOrder", 1)
                .build();

        // when
        final RoutesectionTO to = converter.convertProjection(row);

        // then
        assertEquals("SEC001", to.getSectionId());
        assertEquals("TRACK_A", to.getCommercialTrackId());
        assertEquals("HKI", to.getStationCode());
        assertEquals(42, to.getRoutesetId());
        // Association field must be null
        assertNull(to.getStation());
    }

    @Test
    void nullableFieldsAcceptNull() {
        // given
        final Tuple row = TestTuple.builder()
                .put("sectionId", "SEC001")
                .put("commercialTrackId", null)
                .put("stationCode", "HKI")
                .put("routesetId", 1L)
                .put("sectionOrder", 1)
                .build();

        // when
        final RoutesectionTO to = converter.convertProjection(row);

        // then
        assertNull(to.getCommercialTrackId());
    }

    @Test
    void projectionOutputMatchesEntityConversion() {
        // given
        final var entity = new Routesection();
        entity.sectionId = "SEC002";
        entity.commercialTrackId = "TRACK_B";
        entity.stationCode = "TPE";
        entity.routesetId = 99L;
        entity.sectionOrder = 3;

        final Tuple row = TestTuple.builder()
                .put("sectionId", "SEC002")
                .put("commercialTrackId", "TRACK_B")
                .put("stationCode", "TPE")
                .put("routesetId", 99L)
                .put("sectionOrder", 3)
                .build();

        // when
        final RoutesectionTO fromEntity = converter.convertEntity(entity);
        final RoutesectionTO fromProjection = converter.convertProjection(row);

        // then
        assertEquals(fromEntity.getSectionId(), fromProjection.getSectionId());
        assertEquals(fromEntity.getCommercialTrackId(), fromProjection.getCommercialTrackId());
        assertEquals(fromEntity.getStationCode(), fromProjection.getStationCode());
        assertEquals(fromEntity.getRoutesetId(), fromProjection.getRoutesetId());
    }
}
