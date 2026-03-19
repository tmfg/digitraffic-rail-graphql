package fi.digitraffic.graphql.rail.factory;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import fi.digitraffic.graphql.rail.entities.RoutesetMessage;
import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.repositories.RoutesetMessageRepository;

@Component
public class RoutesetMessageFactory {

    @Autowired
    private RoutesetMessageRepository routesetMessageRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

    @Transactional
    public long createWithStringTrainNumber(final String trainNumber, final LocalDate departureDate) {
        final long id = idSequence++;
        entityManager.createNativeQuery(
                "INSERT INTO routeset (id, version, message_time, train_number, departure_date, route_type, client_system, message_id) VALUES (?, ?, NOW(), ?, ?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, 1L)
                .setParameter(3, trainNumber)
                .setParameter(4, departureDate)
                .setParameter(5, "A")
                .setParameter(6, "test")
                .setParameter(7, String.valueOf(id))
                .executeUpdate();
        return id;
    }
}
