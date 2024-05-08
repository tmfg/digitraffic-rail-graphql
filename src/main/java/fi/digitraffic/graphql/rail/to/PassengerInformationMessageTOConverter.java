package fi.digitraffic.graphql.rail.to;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.PassengerInformationAudio;
import fi.digitraffic.graphql.rail.entities.PassengerInformationStation;
import fi.digitraffic.graphql.rail.entities.PassengerInformationVideo;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationStationTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationTextContentTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoTO;

@Component
public class PassengerInformationMessageTOConverter {
    public PassengerInformationMessageTO convert(final Tuple tuple) {
        final Set<PassengerInformationStation> stations = tuple.get(QPassengerInformationMessage.passengerInformationMessage.stations);
        final List<PassengerInformationStationTO> stationsTO =
                stations.stream().map(station -> new PassengerInformationStationTO(station.stationShortCode)).toList();

        final PassengerInformationAudio audio = tuple.get(QPassengerInformationMessage.passengerInformationMessage.audio);
        final PassengerInformationAudioTO audioTO = new PassengerInformationAudioTO(new PassengerInformationTextContentTO(audio.textFi, audio.textSv,
                audio.textEn));

        final PassengerInformationVideo video = tuple.get(QPassengerInformationMessage.passengerInformationMessage.video);
        final PassengerInformationVideoTO videoTO = new PassengerInformationVideoTO(new PassengerInformationTextContentTO(video.textFi, video.textSv,
                video.textEn));

        return new PassengerInformationMessageTO(tuple.get(QPassengerInformationMessage.passengerInformationMessage.id),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.version),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.creationDateTime),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.startValidity),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.endValidity),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.trainDepartureDate),
                tuple.get(QPassengerInformationMessage.passengerInformationMessage.trainNumber).intValue(),
                stationsTO,
                audioTO,
                videoTO);
    }
}


