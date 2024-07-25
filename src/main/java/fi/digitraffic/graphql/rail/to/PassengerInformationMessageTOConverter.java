package fi.digitraffic.graphql.rail.to;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.entities.QPassengerInformationMessage;
import fi.digitraffic.graphql.rail.model.DayOfWeekTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationTextContentTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoDeliveryRulesTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoTO;

@Component
public class PassengerInformationMessageTOConverter {

    protected PassengerInformationVideoTO createPassengerInformationVideoTO(final PassengerInformationMessage message) {
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


