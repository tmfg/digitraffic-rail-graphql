package fi.digitraffic.graphql.rail.links;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.repositories.TrainTrackingMessageRepository;
import fi.digitraffic.graphql.rail.to.TrainTrackingTOConverter;

@Component
public class TrainToTrainTrackingMessagesLink extends OneToManyLink<TrainId, TrainTO, TrainTrackingMessage, TrainTrackingMessageTO> {
    @Autowired
    private TrainTrackingMessageRepository trainTrackingMessageRepository;

    @Autowired
    private TrainTrackingTOConverter trainTrackingTOConverter;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "trainTrackingMessages";
    }

    @Override
    public TrainId createKeyFromParent(TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber().longValue(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(TrainTrackingMessage child) {
        Long trainNumber;
        try {
            trainNumber = Long.parseLong(child.trainId.trainNumber);
        } catch (NumberFormatException e) {
            trainNumber = -1111L;
        }
        return new TrainId(trainNumber, child.trainId.virtualDepartureDate);
    }

    @Override
    public TrainTrackingMessageTO createChildTOToFromChild(TrainTrackingMessage child) {
        return trainTrackingTOConverter.convert(child);
    }

    @Override
    public List<TrainTrackingMessage> findChildrenByKeys(List<TrainId> keys) {
        List<StringVirtualDepartureDateTrainId> stringTrainIds = keys.stream().map(s -> new StringVirtualDepartureDateTrainId(s.trainNumber.toString(), s.departureDate)).collect(Collectors.toList());
        return trainTrackingMessageRepository.findAllByTrainIds(stringTrainIds);
    }
}
