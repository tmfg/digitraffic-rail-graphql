package fi.digitraffic.graphql.rail.to;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import jakarta.persistence.Tuple;

class PassengerInformationMessageStationTOConverterTest {

    private final PassengerInformationMessageStationTOConverter converter = new PassengerInformationMessageStationTOConverter();

    @Test
    void allScalarFieldsAreMappedFromProjectionRow() {
        // given
        final Tuple row = TestTuple.builder()
                .put("stationShortCode", "HKI")
                .put("messageId", "MSG001")
                .put("messageVersion", 3)
                .build();

        // when
        final PassengerInformationMessageStationTO to = converter.convertProjection(row);

        // then
        assertEquals("HKI", to.getStationShortCode());
        assertEquals("MSG001", to.getMessageId());
        assertEquals(3, to.getMessageVersion());
        // Association fields must be null
        assertNull(to.getStation());
        assertNull(to.getMessage());
    }

    @Test
    void projectionOutputMatchesEntityConversion() {
        // given
        final var entity = new PassengerInformationMessageStation();
        entity.stationShortCode = "TPE";
        entity.messageId = "MSG002";
        entity.messageVersion = 5;

        final Tuple row = TestTuple.builder()
                .put("stationShortCode", "TPE")
                .put("messageId", "MSG002")
                .put("messageVersion", 5)
                .build();

        // when
        final PassengerInformationMessageStationTO fromEntity = converter.convertEntity(entity);
        final PassengerInformationMessageStationTO fromProjection = converter.convertProjection(row);

        // then
        assertEquals(fromEntity.getStationShortCode(), fromProjection.getStationShortCode());
        assertEquals(fromEntity.getMessageId(), fromProjection.getMessageId());
        assertEquals(fromEntity.getMessageVersion(), fromProjection.getMessageVersion());
    }
}
