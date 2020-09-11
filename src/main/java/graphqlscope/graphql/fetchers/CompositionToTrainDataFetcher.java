package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Train;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.fetchers.base.OneToOneDataFetcher;
import graphqlscope.graphql.model.CompositionTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.TrainRepository;
import graphqlscope.graphql.to.TrainTOConverter;

@Component
public class CompositionToTrainDataFetcher extends OneToOneDataFetcher<TrainId, CompositionTO, Train, TrainTO> {
    @Autowired
    private TrainTOConverter trainTOConverter;

    @Autowired
    private TrainRepository trainRepository;

    @Override
    public String getTypeName() {
        return "Composition";
    }

    @Override
    public String getFieldName() {
        return "train";
    }

    @Override
    public TrainId createKeyFromParent(CompositionTO compositionTO) {
        return new TrainId(compositionTO.getTrainNumber().longValue(), compositionTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(Train child) {
        return child.id;
    }

    @Override
    public TrainTO createChildTOToFromChild(Train child) {
        return trainTOConverter.convert(child);
    }

    @Override
    public List<Train> findChildrenByKeys(List<TrainId> keys) {
        return trainRepository.findAllById(keys);
    }

}
