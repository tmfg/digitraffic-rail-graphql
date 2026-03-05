package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToOneLinkJpql;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.to.StationTOConverter;

/**
 * JPQL implementation: PassengerInformationMessageStation → station (OneToOne).
 */
@Component
public class PassengerInformationMessageStationToStationLink
        extends OneToOneLinkJpql<String, PassengerInformationMessageStationTO, Station, StationTO> {

    @Autowired
    private StationTOConverter stationTOConverter;

    @Override
    public String getTypeName() {
        return "PassengerInformationMessageStation";
    }

    @Override
    public String getFieldName() {
        return "station";
    }

    @Override
    public String createKeyFromParent(final PassengerInformationMessageStationTO parent) {
        return parent.getStationShortCode();
    }

    @Override
    public String createKeyFromChild(final StationTO child) {
        return child.getShortCode();
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
}

