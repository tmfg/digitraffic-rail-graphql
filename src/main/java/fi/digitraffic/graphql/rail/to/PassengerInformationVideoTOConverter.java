package fi.digitraffic.graphql.rail.to;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.QPassengerInformationVideo;
import fi.digitraffic.graphql.rail.model.DayOfWeekTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationTextContentTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoDeliveryRulesTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoTO;

@Component
public class PassengerInformationVideoTOConverter {
    public PassengerInformationVideoTO convert(final Tuple tuple) {
        return new PassengerInformationVideoTO(
                new PassengerInformationTextContentTO(tuple.get(QPassengerInformationVideo.passengerInformationVideo.textFi),
                        tuple.get(QPassengerInformationVideo.passengerInformationVideo.textSv),
                        tuple.get(QPassengerInformationVideo.passengerInformationVideo.textEn)),
                new PassengerInformationVideoDeliveryRulesTO(tuple.get(QPassengerInformationVideo.passengerInformationVideo.deliveryType),
                        tuple.get(QPassengerInformationVideo.passengerInformationVideo.startDateTime),
                        tuple.get(QPassengerInformationVideo.passengerInformationVideo.endDateTime),
                        tuple.get(QPassengerInformationVideo.passengerInformationVideo.startTime),
                        tuple.get(QPassengerInformationVideo.passengerInformationVideo.endTime),
                        tuple.get(QPassengerInformationVideo.passengerInformationVideo.weekDays).stream()
                                .map(weekDay -> DayOfWeekTO.valueOf(weekDay)).collect(
                                        Collectors.toList())),
                tuple.get(QPassengerInformationVideo.passengerInformationVideo.messageId),
                tuple.get(QPassengerInformationVideo.passengerInformationVideo.messageVersion)
        );
    }
}
