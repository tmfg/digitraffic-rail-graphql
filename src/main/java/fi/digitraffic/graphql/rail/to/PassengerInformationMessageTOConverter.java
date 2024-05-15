package fi.digitraffic.graphql.rail.to;

import java.util.List;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationStationTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationTextContentTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoTO;

@Component
public class PassengerInformationMessageTOConverter {

    public PassengerInformationMessageTO convert(final Tuple tuple) {
        final PassengerInformationMessage message = tuple.get(QPassengerInformationMessage.passengerInformationMessage);

        if (message == null) {
            return null;
        }

        final List<PassengerInformationStationTO> stationsTO = message.stations != null ?
                                                               message.stations.stream()
                                                                       .map(station -> new PassengerInformationStationTO(station.stationShortCode))
                                                                       .toList() : null;

        final PassengerInformationAudioTO audioTO = message.audio != null ?
                                                    new PassengerInformationAudioTO(
                                                            new PassengerInformationTextContentTO(message.audio.textFi, message.audio.textSv,
                                                                    message.audio.textEn)) : null;

        final PassengerInformationVideoTO videoTO = message.video != null ?
                                                    new PassengerInformationVideoTO(
                                                            new PassengerInformationTextContentTO(message.video.textFi, message.video.textSv,
                                                                    message.video.textEn)) : null;

        return new PassengerInformationMessageTO(message.id,
                message.version,
                message.creationDateTime,
                message.startValidity,
                message.endValidity,
                message.trainDepartureDate,
                message.trainNumber != null ? message.trainNumber.intValue() : null,
                stationsTO,
                audioTO,
                videoTO);
    }

}


