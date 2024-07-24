package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;

import fi.digitraffic.graphql.rail.entities.QStation;
import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageStationTO;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.StationTOConverter;

@Component
public class PassengerInformationMessageStationToStationLink extends OneToOneLink<String, PassengerInformationMessageStationTO, Station, StationTO> {
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
    public String createKeyFromParent(final PassengerInformationMessageStationTO passengerInformationMessageStationTO) {
        return passengerInformationMessageStationTO.getStationShortCode();
    }

    @Override
    public String createKeyFromChild(final StationTO stationTO) {
        return stationTO.getShortCode();
    }

    @Override
    public StationTO createChildTOFromTuple(final Tuple tuple) {
        return stationTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return Station.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.STATION;
    }

    @Override
    public EntityPath getEntityTable() {
        return QStation.station;
    }

    @Override
    public BooleanExpression createWhere(final List<String> keys) {
        return QStation.station.shortCode.in(keys);
    }
}

