package fi.digitraffic.graphql.rail.factory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class WagonFactory {

    @PersistenceContext
    private EntityManager entityManager;

    private long idSequence = 5_000L;

    @Transactional
    public long create(final long journeySectionId) {
        final long id = idSequence++;
        entityManager.createNativeQuery(
                "INSERT INTO wagon (id, journeysection, location, sales_number, length) VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, journeySectionId)
                .setParameter(3, 1)
                .setParameter(4, 1)
                .setParameter(5, 10)
                .executeUpdate();
        return id;
    }
}

