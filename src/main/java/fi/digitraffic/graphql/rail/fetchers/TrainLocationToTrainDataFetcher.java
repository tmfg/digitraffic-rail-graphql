package fi.digitraffic.graphql.rail.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.fetchers.base.OneToOneDataFetcher;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;

@Component
public class TrainLocationToTrainDataFetcher extends OneToOneDataFetcher<TrainId, TrainLocationTO, Train, TrainTO> {
    @Autowired
    private TrainTOConverter trainTOConverter;

    @Autowired
    private TrainRepository trainRepository;

    @Override
    public String getTypeName() {
        return "TrainLocation";
    }

    @Override
    public String getFieldName() {
        return "train";
    }

    @Override
    public TrainId createKeyFromParent(TrainLocationTO trainLocationTO) {
        return new TrainId(trainLocationTO.getTrainNumber().longValue(), trainLocationTO.getDepartureDate());
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
