package fi.digitraffic.graphql.rail.to;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;


import fi.digitraffic.graphql.rail.entities.PassengerInformationVideo;
import fi.digitraffic.graphql.rail.model.DayOfWeekTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationTextContentTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoDeliveryRulesTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationVideoTO;

@Component
public class PassengerInformationVideoTOConverter {

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
