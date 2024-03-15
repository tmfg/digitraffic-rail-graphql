package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.JourneySection;
import fi.digitraffic.graphql.rail.entities.QJourneySection;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.repositories.TrainIdOptimizer;
import fi.digitraffic.graphql.rail.to.JourneySectionTOConverter;

@Component
public class CompositionToJourneySectionsLink extends OneToManyLink<TrainId, CompositionTO, JourneySection, JourneySectionTO> {
    @Autowired
    private JourneySectionTOConverter journeySectionTOConverter;

    @Override
    public String getTypeName() {
        return "Composition";
    }

    @Override
    public String getFieldName() {
        return "journeySections";
    }

    @Override
    public TrainId createKeyFromParent(final CompositionTO parent) {
        return new TrainId(parent.getTrainNumber(), parent.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(final JourneySectionTO journeySectionTO) {
        return new TrainId(journeySectionTO.getTrainNumber(), journeySectionTO.getDepartureDate());
    }

    @Override
    public JourneySectionTO createChildTOFromTuple(final Tuple tuple) {
        return journeySectionTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return JourneySection.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.JOURNEY_SECTION;
    }

    @Override
    public EntityPath getEntityTable() {
        return QJourneySection.journeySection;
    }

    @Override
    public BooleanExpression createWhere(List<TrainId> keys) {
        return TrainIdOptimizer.optimize(QJourneySection.journeySection.trainId, keys);
    }
}
