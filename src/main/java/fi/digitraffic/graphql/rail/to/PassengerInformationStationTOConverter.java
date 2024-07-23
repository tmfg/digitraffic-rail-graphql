package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.QPassengerInformationStation;
import fi.digitraffic.graphql.rail.model.PassengerInformationStationTO;

@Component
public class PassengerInformationStationTOConverter {

    public PassengerInformationStationTO convert(final Tuple tuple) {

        return new PassengerInformationStationTO(tuple.get(QPassengerInformationStation.passengerInformationStation.stationShortCode),
                null,
                tuple.get(QPassengerInformationStation.passengerInformationStation.messageId),
                tuple.get(QPassengerInformationStation.passengerInformationStation.messageVersion));
    }
}
