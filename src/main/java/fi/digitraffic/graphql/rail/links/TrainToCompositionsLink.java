package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Composition;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.links.base.TrainIdWhereClause;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.CompositionTOConverter;

@Component
public class TrainToCompositionsLink extends OneToManyLink<TrainId, TrainTO, Composition, CompositionTO> {

    private final CompositionTOConverter compositionTOConverter;

    public TrainToCompositionsLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                   final JpqlOrderByBuilder jpqlOrderByBuilder,
                                   @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                   final CompositionTOConverter compositionTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.compositionTOConverter = compositionTOConverter;
    }

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "compositions";
    }

    @Override
    public TrainId createKeyFromParent(final TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(final CompositionTO compositionTO) {
        return new TrainId(compositionTO.getTrainNumber(), compositionTO.getDepartureDate());
    }

    @Override
    public CompositionTO createChildTOFromEntity(final Composition entity) {
        return compositionTOConverter.convertEntity(entity);
    }

    @Override
    public Class<Composition> getEntityClass() {
        return Composition.class;
    }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<TrainId> keys) {
        return TrainIdWhereClause.build(getEntityAlias(), "id.departureDate", "id.trainNumber", keys);
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".id.trainNumber ASC";
    }
}

