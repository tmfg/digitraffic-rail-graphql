package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.JourneySection;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.fetchers.base.OneToManyDataFetcher;
import graphqlscope.graphql.model.CompositionTO;
import graphqlscope.graphql.model.JourneySectionTO;
import graphqlscope.graphql.repositories.JourneySectionRepository;
import graphqlscope.graphql.to.JourneySectionTOConverter;

@Component
public class CompositionToJourneySectionsDataFetcher extends OneToManyDataFetcher<TrainId, CompositionTO, JourneySection, JourneySectionTO> {
    @Autowired
    private JourneySectionRepository journeySectionRepository;

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
    public TrainId createKeyFromChild(JourneySection child) {
        return new TrainId(child.trainId.trainNumber, child.trainId.departureDate);
    }

    @Override
    public JourneySectionTO createChildTOToFromChild(JourneySection child) {
        return journeySectionTOConverter.convert(child);
    }

    @Override
    public List<JourneySection> findChildrenByKeys(List<TrainId> keys) {
        return journeySectionRepository.findAllByTrainIds(keys);
    }
}
