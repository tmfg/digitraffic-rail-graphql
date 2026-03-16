package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.JourneySection;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.links.base.TrainIdWhereClause;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.JourneySectionTOConverter;

@Component
public class CompositionToJourneySectionsLink extends OneToManyLink<TrainId, CompositionTO, JourneySection, JourneySectionTO> {

    private final JourneySectionTOConverter journeySectionTOConverter;

    public CompositionToJourneySectionsLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                            final JpqlOrderByBuilder jpqlOrderByBuilder,
                                            @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                            final JourneySectionTOConverter journeySectionTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.journeySectionTOConverter = journeySectionTOConverter;
    }

    @Override
    public String getTypeName() { return "Composition"; }

    @Override
    public String getFieldName() { return "journeySections"; }

    @Override
    public TrainId createKeyFromParent(final CompositionTO compositionTO) {
        return new TrainId(compositionTO.getTrainNumber(), compositionTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(final JourneySectionTO journeySectionTO) {
        return new TrainId(journeySectionTO.getTrainNumber(), journeySectionTO.getDepartureDate());
    }

    @Override
    public JourneySectionTO createChildTOFromEntity(final JourneySection entity) {
        return journeySectionTOConverter.convertEntity(entity);
    }

    @Override
    public Class<JourneySection> getEntityClass() { return JourneySection.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<TrainId> keys) {
        return TrainIdWhereClause.build(getEntityAlias(), "trainId.departureDate", "trainId.trainNumber", keys);
    }
}

