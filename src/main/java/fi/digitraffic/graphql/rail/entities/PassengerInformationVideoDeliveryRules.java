package fi.digitraffic.graphql.rail.entities;

import java.time.ZonedDateTime;
import java.util.List;

public class PassengerInformationVideoDeliveryRules {
    public String deliveryType;
    public ZonedDateTime startDateTime;
    public ZonedDateTime endDateTime;
    public String startTime;
    public String endTime;
    public List<String> weekDays;

    public PassengerInformationVideoDeliveryRules(final String deliveryType, final ZonedDateTime startDateTime, final ZonedDateTime endDateTime,
                                                  final String startTime,
                                                  final String endTime, final List<String> weekDays) {
        this.deliveryType = deliveryType;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekDays = weekDays;
    }
}
