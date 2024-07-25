package fi.digitraffic.graphql.rail.to;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.QPassengerInformationAudio;
import fi.digitraffic.graphql.rail.model.DayOfWeekTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioDeliveryRulesTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationTextContentTO;

@Component
public class PassengerInformationAudioTOConverter {

    public PassengerInformationAudioTO convert(final Tuple tuple) {
        return new PassengerInformationAudioTO(
                new PassengerInformationTextContentTO(tuple.get(QPassengerInformationAudio.passengerInformationAudio.textFi),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.textSv),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.textEn)),
                new PassengerInformationAudioDeliveryRulesTO(tuple.get(QPassengerInformationAudio.passengerInformationAudio.deliveryType),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.eventType),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.startDateTime),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.endDateTime),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.startTime),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.endTime),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.weekDays).stream()
                                .map(weekDay -> DayOfWeekTO.valueOf(weekDay)).collect(
                                        Collectors.toList()), tuple.get(QPassengerInformationAudio.passengerInformationAudio.deliveryAt),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.repetitions),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.repeatEvery)),
                tuple.get(QPassengerInformationAudio.passengerInformationAudio.messageId),
                tuple.get(QPassengerInformationAudio.passengerInformationAudio.messageVersion), null
        );
    }
}