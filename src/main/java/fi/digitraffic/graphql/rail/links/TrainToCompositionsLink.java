package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.Composition;
import fi.digitraffic.graphql.rail.entities.QComposition;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.repositories.TrainIdOptimizer;
import fi.digitraffic.graphql.rail.to.CompositionTOConverter;

@Component
public class TrainToCompositionsLink extends OneToManyLink<TrainId, TrainTO, Composition, CompositionTO> {
    @Autowired
    private CompositionTOConverter compositionTOConverter;

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
    public CompositionTO createChildTOFromTuple(final Tuple tuple) {
        return compositionTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return Composition.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.COMPOSITION;
    }

    @Override
    public EntityPath getEntityTable() {
        return QComposition.composition;
    }

    @Override
    public BooleanExpression createWhere(List<TrainId> keys) {
        return TrainIdOptimizer.optimize(QComposition.composition.id, keys);
    }
}
