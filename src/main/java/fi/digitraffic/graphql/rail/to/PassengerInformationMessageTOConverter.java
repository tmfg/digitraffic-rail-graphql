package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import jakarta.persistence.Tuple;

@Component
public class PassengerInformationMessageTOConverter {


    public PassengerInformationMessageTO convertEntity(final PassengerInformationMessage entity) {
        return new PassengerInformationMessageTO(
                entity.id.id,
                entity.id.version,
                entity.creationDateTime,
                entity.startValidity,
                entity.endValidity,
                entity.trainDepartureDate,
                entity.trainNumber != null ? entity.trainNumber.intValue() : null,
                null,
                null,
                null,
                null);
    }

    /**
     * Converts a JPQL Tuple row to a PassengerInformationMessageTO.
     * Alias names must match the projection expression in TrainToPassengerInformationMessagesLink.
     */
    public PassengerInformationMessageTO convertProjection(final Tuple row) {
        final Long trainNumber = row.get("trainNumber", Long.class);
        return new PassengerInformationMessageTO(
                row.get("id", String.class),
                row.get("version", Integer.class),
                row.get("creationDateTime", java.time.ZonedDateTime.class),
                row.get("startValidity", java.time.ZonedDateTime.class),
                row.get("endValidity", java.time.ZonedDateTime.class),
                row.get("trainDepartureDate", java.time.LocalDate.class),
                trainNumber != null ? trainNumber.intValue() : null,
                null, null, null, null);                                  // train, messageStations, audio, video
    }
}


