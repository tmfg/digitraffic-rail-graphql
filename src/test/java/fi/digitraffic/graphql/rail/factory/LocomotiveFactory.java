package fi.digitraffic.graphql.rail.factory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class LocomotiveFactory {

    @PersistenceContext
    private EntityManager entityManager;

    private long idSequence = 6_000L;

    @Transactional
    public long create(final long journeySectionId) {
        final long id = idSequence++;
        entityManager.createNativeQuery(
                "INSERT INTO locomotive (id, journeysection, location, locomotive_type, power_type_abbreviation) VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, journeySectionId)
                .setParameter(3, 1)
                .setParameter(4, "Sr2")
                .setParameter(5, "S")
                .executeUpdate();
        return id;
    }
}

