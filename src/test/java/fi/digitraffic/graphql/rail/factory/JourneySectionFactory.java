package fi.digitraffic.graphql.rail.factory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.Train;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Creates JourneySection test data via native SQL.
 * JourneySection has a protected constructor so it cannot be instantiated directly.
 *
 * The journey_section table columns (Spring snake_case naming):
 *   id, departure_date, train_number, attap_id, saap_attap_id, total_length, maximum_speed
 */
@Component
public class JourneySectionFactory {

    @PersistenceContext
    private EntityManager entityManager;

    private long idSequence = 1_000L;

    @Transactional
    public long create(final Train train, final int maximumSpeed, final int totalLength,
                       final long attapId, final long saapAttapId) {
        final long id = idSequence++;
        entityManager.createNativeQuery(
                "INSERT INTO journey_section " +
                "(id, departure_date, train_number, attap_id, saap_attap_id, total_length, maximum_speed) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, train.id.departureDate.toString())
                .setParameter(3, train.id.trainNumber)
                .setParameter(4, attapId)
                .setParameter(5, saapAttapId)
                .setParameter(6, totalLength)
                .setParameter(7, maximumSpeed)
                .executeUpdate();
        return id;
    }
}

