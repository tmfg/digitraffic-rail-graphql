package fi.digitraffic.graphql.rail.factory;

import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.RoutesetMessage;
import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.repositories.RoutesetMessageRepository;

@Component
public class RoutesetMessageFactory {

    @Autowired
    private RoutesetMessageRepository routesetMessageRepository;

    private long idSequence = 1L;

    @Transactional
    public RoutesetMessage create(final Train train) {
        final RoutesetMessage message = new RoutesetMessage();
        message.id = idSequence++;
        message.version = 1L;
        message.messageTime = train.id.departureDate.atStartOfDay(ZoneId.of("Europe/Helsinki"));
        message.trainId = new StringVirtualDepartureDateTrainId(
                train.id.trainNumber.toString(), train.id.departureDate);
        message.routeType = "A";
        message.clientSystem = "test";
        message.messageId = String.valueOf(message.id);
        return routesetMessageRepository.save(message);
    }
}
