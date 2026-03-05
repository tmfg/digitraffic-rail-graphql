package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToOneLinkJpql;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.TimeTableRowTO;
import fi.digitraffic.graphql.rail.to.StationTOConverter;

/**
 * JPQL implementation of TimeTableRowToStationLink.
 * Links TimeTableRow to its Station.
 */
@Component
public class TimeTableRowToStationLink extends OneToOneLinkJpql<String, TimeTableRowTO, Station, StationTO> {

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
    public String createKeyFromParent(final TimeTableRowTO timeTableRowTO) {
        return timeTableRowTO.getStationShortCode();
    }

    @Override
    public String createKeyFromChild(final StationTO stationTO) {
        return stationTO.getShortCode();
    }

    @Override
    public StationTO createChildTOFromEntity(final Station entity) {
        return stationTOConverter.convertEntity(entity);
    }

    @Override
    public Class<Station> getEntityClass() {
        return Station.class;
    }

    @Override
    public String createWhereClause(final List<String> keys) {
        return "e.shortCode IN :keys";
    }

    @Override
    public String getDefaultOrderBy() {
        return "e.name ASC";
    }
}

