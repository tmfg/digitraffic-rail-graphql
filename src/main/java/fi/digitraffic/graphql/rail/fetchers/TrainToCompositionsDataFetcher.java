package fi.digitraffic.graphql.rail.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Composition;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.fetchers.base.OneToManyDataFetcher;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.repositories.CompositionRepository;
import fi.digitraffic.graphql.rail.to.CompositionTOConverter;

@Component
public class TrainToCompositionsDataFetcher extends OneToManyDataFetcher<TrainId, TrainTO, Composition, CompositionTO> {
    @Autowired
    private CompositionRepository compositionRepository;

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
    public TrainId createKeyFromParent(TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber().longValue(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(Composition child) {
        return new TrainId(child.id.trainNumber, child.id.departureDate);
    }

    @Override
    public CompositionTO createChildTOToFromChild(Composition child) {
        return compositionTOConverter.convert(child);
    }

    @Override
    public List<Composition> findChildrenByKeys(List<TrainId> keys) {
        return compositionRepository.findAllByTrainIds(keys);
    }
}
