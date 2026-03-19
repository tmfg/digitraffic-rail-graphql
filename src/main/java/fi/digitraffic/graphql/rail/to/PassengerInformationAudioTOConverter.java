package fi.digitraffic.graphql.rail.to;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;


import fi.digitraffic.graphql.rail.entities.PassengerInformationAudio;
import fi.digitraffic.graphql.rail.model.DayOfWeekTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioDeliveryRulesTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationAudioTO;
import fi.digitraffic.graphql.rail.model.PassengerInformationTextContentTO;

@Component
public class PassengerInformationAudioTOConverter {


    public PassengerInformationAudioTO convertEntity(final PassengerInformationAudio entity) {
        return new PassengerInformationAudioTO(
                new PassengerInformationTextContentTO(entity.textFi, entity.textSv, entity.textEn),
                new PassengerInformationAudioDeliveryRulesTO(
                        entity.deliveryType,
                        entity.eventType,
                        entity.startDateTime,
                        entity.endDateTime,
                        entity.startTime,
                        entity.endTime,
                        entity.weekDays != null ? entity.weekDays.stream()
                                .map(DayOfWeekTO::valueOf).collect(Collectors.toList()) : java.util.List.of(),
                        entity.deliveryAt,
                        entity.repetitions,
                        entity.repeatEvery),
                entity.messageId,
                entity.messageVersion
        );
    }
}