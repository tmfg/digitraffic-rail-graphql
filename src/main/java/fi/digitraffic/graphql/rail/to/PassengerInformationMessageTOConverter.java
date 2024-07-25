package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;

@Component
public class PassengerInformationMessageTOConverter {

    public PassengerInformationMessageTO convert(final Tuple tuple) {
        return new PassengerInformationMessageTO(tuple.get(QPassengerInformationMessage.passengerInformationMessage.id.id),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.id.version),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.creationDateTime),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.startValidity),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.endValidity),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.trainDepartureDate),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.trainNumber) != null ?
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.trainNumber).intValue() : null,
                null,
                null,
                null,
                null);
    }

}


