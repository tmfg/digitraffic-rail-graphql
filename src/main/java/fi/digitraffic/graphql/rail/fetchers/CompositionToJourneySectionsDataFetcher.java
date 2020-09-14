package fi.digitraffic.graphql.rail.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.JourneySection;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.fetchers.base.OneToManyDataFetcher;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.repositories.JourneySectionRepository;
import fi.digitraffic.graphql.rail.to.JourneySectionTOConverter;

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
