package fi.digitraffic.graphql.rail.factory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class RoutesectionFactory {

    @PersistenceContext
    private EntityManager entityManager;

    private long idSequence = 8_000L;

    @Transactional
    public long create(final long routesetId, final String stationCode, final String sectionId) {
        final long id = idSequence++;
        entityManager.createNativeQuery(
                "INSERT INTO routesection (id, routeset_id, station_code, section_id, commercial_track_id, section_order) VALUES (?, ?, ?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, routesetId)
                .setParameter(3, stationCode)
                .setParameter(4, sectionId)
                .setParameter(5, "1")
                .setParameter(6, 1)
                .executeUpdate();
        return id;
    }
}

