package fi.digitraffic.graphql.rail.entities;

import java.time.ZonedDateTime;
import java.util.List;

public class PassengerInformationAudioDeliveryRules {
    public String deliveryType;
    public String eventType;
    public ZonedDateTime startDateTime;
    public ZonedDateTime endDateTime;
    public String startTime;
    public String endTime;
    public List<String> weekDays;
    public ZonedDateTime deliveryAt;
    public Integer repetitions;
    public Integer repeatEvery;

    public PassengerInformationAudioDeliveryRules(final String deliveryType, final String eventType, final ZonedDateTime startDateTime,
                                                  final ZonedDateTime endDateTime,
                                                  final String startTime, final String endTime, final List<String> weekDays,
                                                  final ZonedDateTime deliveryAt, final Integer repetitions,
                                                  final Integer repeatEvery) {
        this.deliveryType = deliveryType;
        this.eventType = eventType;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekDays = weekDays;
        this.deliveryAt = deliveryAt;
        this.repetitions = repetitions;
        this.repeatEvery = repeatEvery;
    }
}
