package fi.digitraffic.graphql.rail.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QStation;
import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.to.StationTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class StationsQuery extends BaseQuery<StationTO> {

    @Autowired
    private StationTOConverter stationTOConverter;

    @Override
    public String getQueryName() {
        return "stations";
    }

    @Override
    public Class getEntityClass() {
        return Station.class;
    }

    @Override
    public EntityPath getEntityTable() {
        return QStation.station;
    }

    @Override
    public BooleanExpression createWhereFromArguments(DataFetchingEnvironment dataFetchingEnvironment) {
        return QStation.station.id.ne(-1L);
    }

    @Override
    public StationTO convertEntityToTO(Tuple tuple) {
        return stationTOConverter.convert(tuple);
    }

    @Override
    public OrderSpecifier createDefaultOrder() {
        return new OrderSpecifier(Order.ASC, QStation.station.name);
    }
}
