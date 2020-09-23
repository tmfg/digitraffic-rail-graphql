package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.repositories.StationRepository;
import fi.digitraffic.graphql.rail.to.StationTOConverter;

@Component
public class TrainTrackingMessageToPreviousStationLink extends OneToOneLink<String, TrainTrackingMessageTO, Station, StationTO> {
    @Autowired
    private StationTOConverter stationTOConverter;

    @Autowired
    private StationRepository stationRepository;

    @Override
    public String getTypeName() {
        return "TrainTrackingMessage";
    }

    @Override
    public String getFieldName() {
        return "previousStation";
    }

    @Override
    public String createKeyFromParent(TrainTrackingMessageTO trainTrackingMessageTO) {
        return trainTrackingMessageTO.getPreviousStationShortCode();
    }

    @Override
    public String createKeyFromChild(Station child) {
        return child.shortCode;
    }

    @Override
    public StationTO createChildTOToFromChild(Station child) {
        return stationTOConverter.convert(child);
    }

    @Override
    public List<Station> findChildrenByKeys(List<String> keys) {
        return stationRepository.findByShortCodeIn(keys);
    }
}
