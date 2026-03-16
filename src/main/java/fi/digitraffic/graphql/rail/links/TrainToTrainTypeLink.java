package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainType;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTypeTO;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrainTypeTOConverter;

@Component
public class TrainToTrainTypeLink extends OneToOneLink<Long, TrainTO, TrainType, TrainTypeTO> {

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
    protected KeyWhereClause buildKeyWhereClause(final List<Long> keys) {
        return simpleInClause(getEntityAlias() + ".id IN :keys", keys);
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".name ASC";
    }
}
