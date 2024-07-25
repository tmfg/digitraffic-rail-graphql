package fi.digitraffic.graphql.rail.to;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoTO;

@Component
public class PassengerInformationMessageWithStationsTOConverter extends PassengerInformationMessageTOConverter {

    @Override
    public PassengerInformationMessageTO convert(final Tuple tuple) {
        final PassengerInformationMessage message = tuple.get(QPassengerInformationMessage.passengerInformationMessage);

        if (message == null) {
            return null;
        }

        final PassengerInformationVideoTO videoTO = message.video != null ? createPassengerInformationVideoTO(message) : null;
        
        final String stationShortCodesStr = tuple.get(1, String.class);
        final Set<String> stationShortCodes = Arrays.stream(stationShortCodesStr.split(","))
                .collect(Collectors.toSet());
        final Set<PassengerInformationMessageStationTO> stationsTO = stationShortCodes.stream()
                .map(stationShortCode -> new PassengerInformationMessageStationTO(stationShortCode, null, null, message.id.id, message.id.version))
                .collect(Collectors.toSet());

        return new PassengerInformationMessageTO(message.id.id,
                message.id.version,
                message.creationDateTime,
                message.startValidity,
                message.endValidity,
                message.trainDepartureDate,
                message.trainNumber != null ? message.trainNumber.intValue() : null,
                null,
                stationsTO.stream().toList(),
                null,
                videoTO);
    }
}
