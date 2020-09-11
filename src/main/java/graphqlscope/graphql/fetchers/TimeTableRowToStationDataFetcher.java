package graphqlscope.graphql.fetchers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Station;
import graphqlscope.graphql.fetchers.base.OneToOneDataFetcher;
import graphqlscope.graphql.model.StationTO;
import graphqlscope.graphql.model.TimeTableRowTO;
import graphqlscope.graphql.repositories.StationRepository;
import graphqlscope.graphql.to.StationTOConverter;

@Component
public class TimeTableRowToStationDataFetcher extends OneToOneDataFetcher<String, TimeTableRowTO, Station, StationTO> {
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
