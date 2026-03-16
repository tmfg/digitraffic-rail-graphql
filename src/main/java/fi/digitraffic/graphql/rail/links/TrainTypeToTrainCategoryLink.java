package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainCategory;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.TrainCategoryTO;
import fi.digitraffic.graphql.rail.model.TrainTypeTO;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrainCategoryTOConverter;

@Component
public class TrainTypeToTrainCategoryLink extends OneToOneLink<Long, TrainTypeTO, TrainCategory, TrainCategoryTO> {

    private final TrainCategoryTOConverter trainCategoryTOConverter;

    public TrainTypeToTrainCategoryLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                        final JpqlOrderByBuilder jpqlOrderByBuilder,
                                        @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                        final TrainCategoryTOConverter trainCategoryTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.trainCategoryTOConverter = trainCategoryTOConverter;
    }

    @Override
    public String getTypeName() { return "TrainType"; }

    @Override
    public String getFieldName() { return "trainCategory"; }

    @Override
    public Long createKeyFromParent(final TrainTypeTO trainTypeTO) {
        return (long) trainTypeTO.getTrainCategoryId();
    }

    @Override
    public Long createKeyFromChild(final TrainCategoryTO trainCategoryTO) {
        return (long) trainCategoryTO.getId();
    }

    @Override
    public TrainCategoryTO createChildTOFromEntity(final TrainCategory entity) {
        return trainCategoryTOConverter.convertEntity(entity);
    }

    @Override
    public Class<TrainCategory> getEntityClass() { return TrainCategory.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<Long> keys) {
        return simpleInClause(getEntityAlias() + ".id IN :keys", keys);
    }
}
