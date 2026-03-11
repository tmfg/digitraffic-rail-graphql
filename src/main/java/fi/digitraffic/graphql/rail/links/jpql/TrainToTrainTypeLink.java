package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainType;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToOneLinkJpql;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTypeTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrainTypeTOConverter;

/**
 * JPQL implementation of TrainToTrainTypeLink.
 * Links Train to its TrainType via trainTypeId.
 */
@Component
public class TrainToTrainTypeLink extends OneToOneLinkJpql<Long, TrainTO, TrainType, TrainTypeTO> {

    private final TrainTypeTOConverter trainTypeTOConverter;

    public TrainToTrainTypeLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                final JpqlOrderByBuilder jpqlOrderByBuilder,
                                @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                final TrainTypeTOConverter trainTypeTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.trainTypeTOConverter = trainTypeTOConverter;
    }

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "trainType";
    }

    @Override
    public Long createKeyFromParent(final TrainTO trainTO) {
        return (long) trainTO.getTrainTypeId();
    }

    @Override
    public Long createKeyFromChild(final TrainTypeTO trainTypeTO) {
        return (long) trainTypeTO.getId();
    }

    @Override
    public TrainTypeTO createChildTOFromEntity(final TrainType entity) {
        return trainTypeTOConverter.convertEntity(entity);
    }

    @Override
    public Class<TrainType> getEntityClass() {
        return TrainType.class;
    }

    @Override
    public String createWhereClause(final List<Long> keys) {
        return getEntityAlias() + ".id IN :keys";
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".name ASC";
    }
}

