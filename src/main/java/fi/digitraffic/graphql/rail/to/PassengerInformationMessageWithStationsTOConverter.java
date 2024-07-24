package fi.digitraffic.graphql.rail.to;

import java.util.List;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioTO;
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

        final PassengerInformationAudioTO audioTO = message.audio != null ? createPassengerInformationAudioTO(message)
                                                                          : null;

        final PassengerInformationVideoTO videoTO = message.video != null ? createPassengerInformationVideoTO(message)
                                                                          : null;

        final List<PassengerInformationMessageStationTO> stationsTO = message.stations != null ?
                                                                      message.stations.stream()
                                                                              .map(station -> new PassengerInformationMessageStationTO(
                                                                                      station.stationShortCode,
                                                                                      null, null, message.id.id, message.id.version))
                                                                              .toList() : null;

        return new PassengerInformationMessageTO(message.id.id,
                message.id.version,
                message.creationDateTime,
                message.startValidity,
                message.endValidity,
                message.trainDepartureDate,
                message.trainNumber != null ? message.trainNumber.intValue() : null,
                null,
                stationsTO,
                audioTO,
                videoTO);
    }

}
