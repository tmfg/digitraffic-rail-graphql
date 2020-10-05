package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QRouteset;
import fi.digitraffic.graphql.rail.entities.Routeset;
import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.RoutesetMessageTOConverter;

@Component
public class TrainToRoutesetMessagesLink extends OneToManyLink<StringVirtualDepartureDateTrainId, TrainTO, Routeset, RoutesetMessageTO> {
    @Autowired
    private RoutesetMessageTOConverter routesetMessageTOConverter;

    @Override
    public String getTypeName() {
        return "Train";
    }

    @Override
    public String getFieldName() {
        return "routesetMessages";
    }

    @Override
    public StringVirtualDepartureDateTrainId createKeyFromParent(TrainTO trainTO) {
        return new StringVirtualDepartureDateTrainId(trainTO.getTrainNumber().toString(), trainTO.getDepartureDate());
    }

    @Override
    public StringVirtualDepartureDateTrainId createKeyFromChild(RoutesetMessageTO routesetMessageTO) {
        return new StringVirtualDepartureDateTrainId(routesetMessageTO.getTrainNumber(), routesetMessageTO.getDepartureDate());
    }

    @Override
    public RoutesetMessageTO createChildTOFromTuple(Tuple tuple) {
        return routesetMessageTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return Routeset.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.ROUTESET;
    }

    @Override
    public EntityPath getEntityTable() {
        return QRouteset.routeset;
    }

    @Override
    public BooleanExpression createWhere(List<StringVirtualDepartureDateTrainId> keys) {
        return QRouteset.routeset.trainId.in(keys);
    }
}
