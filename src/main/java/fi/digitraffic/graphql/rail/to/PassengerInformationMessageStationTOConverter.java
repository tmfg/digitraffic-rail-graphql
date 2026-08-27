package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import jakarta.persistence.Tuple;

@Component
public class PassengerInformationMessageStationTOConverter {


    public PassengerInformationMessageStationTO convertEntity(final PassengerInformationMessageStation entity) {
        return new PassengerInformationMessageStationTO(
                entity.stationShortCode,
                null,
                null,
                entity.messageId,
                entity.messageVersion);
    }

    /**
     * Converts a JPQL Tuple row to a PassengerInformationMessageStationTO.
     * Alias names must match the projection expression in PassengerInformationMessageToMessageStationLink.
     */
    public PassengerInformationMessageStationTO convertProjection(final Tuple row) {
        return new PassengerInformationMessageStationTO(
                row.get("stationShortCode", String.class),
                null,                                                     // station
                null,                                                     // message
                row.get("messageId", String.class),
                row.get("messageVersion", Integer.class));
    }
}
