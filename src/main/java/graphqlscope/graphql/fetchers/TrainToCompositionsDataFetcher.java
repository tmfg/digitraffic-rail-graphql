package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Composition;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.fetchers.base.OneToManyDataFetcher;
import graphqlscope.graphql.model.CompositionTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.CompositionRepository;
import graphqlscope.graphql.to.CompositionTOConverter;

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
