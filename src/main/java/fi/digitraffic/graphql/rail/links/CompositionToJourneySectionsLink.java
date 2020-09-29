package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.config.graphql.AllFields;
import fi.digitraffic.graphql.rail.entities.JourneySection;
import fi.digitraffic.graphql.rail.entities.QJourneySection;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
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
    public TrainId createKeyFromParent(CompositionTO parent) {
        return new TrainId(parent.getTrainNumber().longValue(), parent.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(JourneySectionTO journeySectionTO) {
        return new TrainId(journeySectionTO.getTrainNumber().longValue(), journeySectionTO.getDepartureDate());
    }

    @Override
    public JourneySectionTO createChildTOFromTuple(Tuple tuple) {
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
        return QJourneySection.journeySection.trainId.in(keys);
    }
}
