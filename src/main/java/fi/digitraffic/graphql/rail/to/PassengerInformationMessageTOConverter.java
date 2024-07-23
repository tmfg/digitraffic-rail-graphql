package fi.digitraffic.graphql.rail.to;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.DayOfWeekTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioDeliveryRulesTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationTextContentTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoDeliveryRulesTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoTO;

@Component
public class PassengerInformationMessageTOConverter {

    private PassengerInformationAudioTO createPassengerInformationAudioTO(final PassengerInformationMessage message) {
        return new PassengerInformationAudioTO(
                new PassengerInformationTextContentTO(message.audio.textFi, message.audio.textSv,
                        message.audio.textEn),
                new PassengerInformationAudioDeliveryRulesTO(message.audio.deliveryType,
                        message.audio.eventType, message.audio.startDateTime,
                        message.audio.endDateTime, message.audio.startTime,
                        message.audio.endTime, message.audio.weekDays.stream()
                        .map(weekDay -> DayOfWeekTO.valueOf(weekDay)).collect(
                                Collectors.toList()), message.audio.deliveryAt,
                        message.audio.repetitions != null ? message.audio.repetitions :
                        null,
                        message.audio.repeatEvery != null ? message.audio.repeatEvery :
                        null)
        );
    }

    private PassengerInformationVideoTO createPassengerInformationVideoTO(final PassengerInformationMessage message) {
        return new PassengerInformationVideoTO(
                new PassengerInformationTextContentTO(message.video.textFi, message.video.textSv,
                        message.video.textEn),
                new PassengerInformationVideoDeliveryRulesTO(message.video.deliveryType,
                        message.video.startDateTime, message.video.endDateTime, message.video.startTime,
                        message.video.endTime,
                        message.video.weekDays.stream()
                                .map(weekDay -> DayOfWeekTO.valueOf(weekDay)).collect(
                                        Collectors.toList())));
    }

    public PassengerInformationMessageTO convert(final Tuple tuple) {
        final PassengerInformationMessage message = tuple.get(QPassengerInformationMessage.passengerInformationMessage);

        if (message == null) {
            return null;
        }

        final PassengerInformationAudioTO audioTO = message.audio != null ? createPassengerInformationAudioTO(message)
                                                                          : null;

        final PassengerInformationVideoTO videoTO = message.video != null ? createPassengerInformationVideoTO(message)
                                                                          : null;

        return new PassengerInformationMessageTO(message.id.id,
                message.id.version,
                message.creationDateTime,
                message.startValidity,
                message.endValidity,
                message.trainDepartureDate,
                message.trainNumber != null ? message.trainNumber.intValue() : null,
                null,
                null,
                audioTO,
                videoTO);
    }

}


