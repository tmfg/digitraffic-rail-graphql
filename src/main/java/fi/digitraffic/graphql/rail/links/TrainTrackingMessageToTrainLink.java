package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;

@Component
public class TrainTrackingMessageToTrainLink extends OneToOneLink<TrainId, TrainTrackingMessageTO, Train, TrainTO> {
    @Autowired
    private TrainTOConverter trainTOConverter;

    @Autowired
    private TrainRepository trainRepository;

    @Override
    public String getTypeName() {
        return "TrainTrackingMessage";
    }

    @Override
    public String getFieldName() {
        return "train";
    }

    @Override
    public TrainId createKeyFromParent(TrainTrackingMessageTO trainTrackingMessageTO) {
        try {
            return new TrainId(Long.parseLong(trainTrackingMessageTO.getTrainNumber()), trainTrackingMessageTO.getDepartureDate());
        } catch (NumberFormatException e) {
            return new TrainId(-1111L, trainTrackingMessageTO.getDepartureDate());
        }
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
