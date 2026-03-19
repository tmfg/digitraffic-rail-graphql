package fi.digitraffic.graphql.rail.factory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.digitraffic.graphql.rail.entities.Train;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class CompositionFactory {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void create(final Train train) {
        entityManager.createNativeQuery(
                "INSERT INTO composition (departure_date, train_number, operator_short_code, operator_uic_code, " +
                "train_category_id, train_type_id, version) VALUES (?, ?, ?, ?, ?, ?, ?)")
                .setParameter(1, train.id.departureDate.toString())
                .setParameter(2, train.id.trainNumber)
                .setParameter(3, train.operatorShortCode)
                .setParameter(4, train.operatorUicCode)
                .setParameter(5, train.trainCategoryId)
                .setParameter(6, train.trainTypeId)
                .setParameter(7, train.version)
                .executeUpdate();
    }
}


