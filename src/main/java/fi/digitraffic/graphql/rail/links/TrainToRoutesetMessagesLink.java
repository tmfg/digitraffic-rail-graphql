package fi.digitraffic.graphql.rail.links;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Routeset;
import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.repositories.RoutesetRepository;
import fi.digitraffic.graphql.rail.to.RoutesetMessageTOConverter;

@Component
public class TrainToRoutesetMessagesLink extends OneToManyLink<TrainId, TrainTO, Routeset, RoutesetMessageTO> {
    @Autowired
    private RoutesetMessageTOConverter routesetMessageTOConverter;

    @Autowired
    private RoutesetRepository routesetRepository;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "routesetMessages";
    }

    @Override
    public TrainId createKeyFromParent(TrainTO trainTO) {
        return new TrainId(trainTO.getTrainNumber().longValue(), trainTO.getDepartureDate());
    }

    @Override
    public TrainId createKeyFromChild(Routeset child) {
        Long trainNumber;
        try {
            trainNumber = Long.parseLong(child.trainId.trainNumber);
        } catch (NumberFormatException e) {
            trainNumber = -1111L;
        }
        return new TrainId(trainNumber, child.trainId.virtualDepartureDate);
    }

    @Override
    public RoutesetMessageTO createChildTOToFromChild(Routeset child) {
        return routesetMessageTOConverter.convert(child);
    }

    @Override
    public List<Routeset> findChildrenByKeys(List<TrainId> keys) {
        List<StringVirtualDepartureDateTrainId> stringTrainIds = keys.stream().map(s -> new StringVirtualDepartureDateTrainId(s.trainNumber.toString(), s.departureDate)).collect(Collectors.toList());
        return routesetRepository.findAllByTrainIds(stringTrainIds);
    }
}
