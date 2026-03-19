package fi.digitraffic.graphql.rail.factory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class TrackSectionFactory {

    @PersistenceContext
    private EntityManager entityManager;

    private long idSequence = 7_000L;

    @Transactional
    public long create(final String stationShortCode, final String trackSectionCode) {
        final long id = idSequence++;
        entityManager.createNativeQuery(
                "INSERT INTO track_section (id, station, track_section_code) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, stationShortCode)
                .setParameter(3, trackSectionCode)
                .executeUpdate();
        return id;
    }

    @Transactional
    public long createRange(final long trackSectionId) {
        final long id = idSequence++;
        entityManager.createNativeQuery(
                "INSERT INTO track_range (id, track_section_id, start_track, end_track, start_kilometres, end_kilometres, start_metres, end_metres) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, trackSectionId)
                .setParameter(3, "001")
                .setParameter(4, "002")
                .setParameter(5, 10)
                .setParameter(6, 20)
                .setParameter(7, 0)
                .setParameter(8, 500)
                .executeUpdate();
        return id;
    }
}

