package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QStation;
import fi.digitraffic.graphql.rail.entities.QTrackSection;
import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.TrackSectionTO;
import fi.digitraffic.graphql.rail.to.StationTOConverter;

@Component
public class TrackSectionToStationLink extends OneToOneLink<String, TrackSectionTO, Station, StationTO> {
    @Autowired
    private StationTOConverter stationTOConverter;

    @Override
    public String getTypeName() {
        return "TrackSection";
    }

    @Override
    public String getFieldName() {
        return "station";
    }

    @Override
    public String createKeyFromParent(final TrackSectionTO trackSectionTO) {
        return trackSectionTO.getStationShortCode();
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
    public Class<Station> getEntityClass() {
        return Station.class;
    }

    @Override
    public EntityPath getEntityTable() {
        return QStation.station;
    }

    @Override
    public BooleanExpression createWhere(final List<String> keys) {
        return QStation.station.shortCode.in(keys);
    }

    @Override
    public OrderSpecifier createDefaultOrder() {
        return new OrderSpecifier(Order.ASC, QStation.station.id);
    }

    @Override
    public List<Expression<?>> columnsNeededFromParentTable() {
        return List.of(QTrackSection.trackSection.stationShortCode);
    }
}
