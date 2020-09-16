package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.entities.TrainLocation;
import fi.digitraffic.graphql.rail.links.base.OneToManyDataFetcher;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.repositories.TrainLocationRepository;
import fi.digitraffic.graphql.rail.to.TrainLocationTOConverter;

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
