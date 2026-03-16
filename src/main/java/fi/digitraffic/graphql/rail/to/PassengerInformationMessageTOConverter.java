package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;


import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;

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

}


