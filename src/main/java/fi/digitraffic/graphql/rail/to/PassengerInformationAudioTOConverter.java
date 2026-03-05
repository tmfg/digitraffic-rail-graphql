package fi.digitraffic.graphql.rail.to;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;

import fi.digitraffic.graphql.rail.entities.PassengerInformationAudio;
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
                                .map(DayOfWeekTO::valueOf).collect(
                                        Collectors.toList()), tuple.get(QPassengerInformationAudio.passengerInformationAudio.deliveryAt),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.repetitions),
                        tuple.get(QPassengerInformationAudio.passengerInformationAudio.repeatEvery)),
                tuple.get(QPassengerInformationAudio.passengerInformationAudio.messageId),
                tuple.get(QPassengerInformationAudio.passengerInformationAudio.messageVersion)
        );
    }

    /**
     * Converts a PassengerInformationAudio entity to PassengerInformationAudioTO.
     * Used by JPQL-based links. The entity uses @PostLoad to populate transient fields.
     */
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