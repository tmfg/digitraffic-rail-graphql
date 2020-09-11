package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.entities.TrainLocation;
import graphqlscope.graphql.fetchers.base.OneToManyDataFetcher;
import graphqlscope.graphql.model.TrainLocationTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.TrainLocationRepository;
import graphqlscope.graphql.to.TrainLocationTOConverter;

@Component
public class TrainToTrainLocationsDataFetcher extends OneToManyDataFetcher<TrainId, TrainTO, TrainLocation, TrainLocationTO> {
    @Autowired
    private TrainLocationTOConverter trainLocationTOConverter;

    @Autowired
    private TrainLocationRepository trainLocationRepository;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "trainLocations";
    }

    @Override
    public TrainId createKeyFromParent(TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber().longValue(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(TrainLocation child) {
        return new TrainId(child.trainLocationId.trainNumber, child.trainLocationId.departureDate);
    }

    @Override
    public TrainLocationTO createChildTOToFromChild(TrainLocation child) {
        return trainLocationTOConverter.convert(child);
    }

    @Override
    public List<TrainLocation> findChildrenByKeys(List<TrainId> keys) {
        return trainLocationRepository.findAllByTrainIds(keys);
    }
}
