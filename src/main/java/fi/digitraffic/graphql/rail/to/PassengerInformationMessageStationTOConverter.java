package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;


import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;

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
}
