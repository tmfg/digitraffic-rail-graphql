package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.config.graphql.AllFields;
import fi.digitraffic.graphql.rail.entities.QStation;
import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.to.StationTOConverter;

@Component
public class TrainTrackingMessageToNextStationLink extends OneToOneLink<String, TrainTrackingMessageTO, Station, StationTO> {
    @Autowired
    private StationTOConverter stationTOConverter;

    @Override
    public String getTypeName() {
        return "TrainTrackingMessage";
    }

    @Override
    public String getFieldName() {
        return "nextStation";
    }

    @Override
    public String createKeyFromParent(TrainTrackingMessageTO trainTrackingMessageTO) {
        return trainTrackingMessageTO.getNextStationShortCode();
    }

    @Override
    public String createKeyFromChild(StationTO stationTO) {
        return stationTO.getShortCode();
    }

    @Override
    public StationTO createChildTOFromTuple(Tuple tuple) {
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
    public BooleanExpression createWhere(List<String> keys) {
        return QStation.station.shortCode.in(keys);
    }
}
