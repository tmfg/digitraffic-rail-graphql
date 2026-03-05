package fi.digitraffic.graphql.rail.to;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.PassengerInformationVideo;
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
                                .map(DayOfWeekTO::valueOf).toList()),
                tuple.get(QPassengerInformationVideo.passengerInformationVideo.messageId),
                tuple.get(QPassengerInformationVideo.passengerInformationVideo.messageVersion)
        );
    }

    /**
     * Converts a PassengerInformationVideo entity to PassengerInformationVideoTO.
     * Used by JPQL-based links.
     */
    public PassengerInformationVideoTO convertEntity(final PassengerInformationVideo entity) {
        return new PassengerInformationVideoTO(
                new PassengerInformationTextContentTO(entity.textFi, entity.textSv, entity.textEn),
                new PassengerInformationVideoDeliveryRulesTO(
                        entity.deliveryType,
                        entity.startDateTime,
                        entity.endDateTime,
                        entity.startTime,
                        entity.endTime,
                        entity.weekDays != null ? entity.weekDays.stream()
                                .map(DayOfWeekTO::valueOf).toList() : java.util.List.of()),
                entity.messageId,
                entity.messageVersion
        );
    }
}
