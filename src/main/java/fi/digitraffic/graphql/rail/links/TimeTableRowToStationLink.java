package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.repositories.StationRepository;
import fi.digitraffic.graphql.rail.to.StationTOConverter;

@Component
public class TimeTableRowToStationLink extends OneToOneLink<String, TimeTableRowTO, Station, StationTO> {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationTOConverter stationTOConverter;

    @Override
    public String getTypeName() {
        return "TimeTableRow";
    }

    @Override
    public String getFieldName() {
        return "station";
    }

    @Override
    public String createKeyFromParent(TimeTableRowTO timeTableRowTO) {
        return timeTableRowTO.getStationShortCode();
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
