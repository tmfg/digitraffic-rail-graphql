package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessageStation;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;

@Component
public class PassengerInformationMessageStationTOConverter {

    public PassengerInformationMessageStationTO convert(final Tuple tuple) {
        return new PassengerInformationMessageStationTO(
                tuple.get(QPassengerInformationMessageStation.passengerInformationMessageStation.stationShortCode),
                null,
                null,
                tuple.get(QPassengerInformationMessageStation.passengerInformationMessageStation.messageId),
                tuple.get(QPassengerInformationMessageStation.passengerInformationMessageStation.messageVersion));
    }

    /**
     * Converts a PassengerInformationMessageStation entity to PassengerInformationMessageStationTO.
     * Used by JPQL-based links.
     */
    public PassengerInformationMessageStationTO convertEntity(final PassengerInformationMessageStation entity) {
        return new PassengerInformationMessageStationTO(
                entity.stationShortCode,
                null,
                null,
                entity.messageId,
                entity.messageVersion);
    }
}
