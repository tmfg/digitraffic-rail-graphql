package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QRoutesetMessage;
import fi.digitraffic.graphql.rail.entities.RoutesetMessage;
import fi.digitraffic.graphql.rail.entities.StringVirtualDepartureDateTrainId;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.repositories.TrainIdOptimizer;
import fi.digitraffic.graphql.rail.to.RoutesetMessageTOConverter;

@Component
public class TrainToRoutesetMessagesLink extends OneToManyLink<StringVirtualDepartureDateTrainId, TrainTO, RoutesetMessage, RoutesetMessageTO> {
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
    public StringVirtualDepartureDateTrainId createKeyFromParent(final TrainTO trainTO) {
        return new StringVirtualDepartureDateTrainId(String.valueOf(trainTO.getTrainNumber()), trainTO.getDepartureDate());
    }

    @Override
    public StringVirtualDepartureDateTrainId createKeyFromChild(final RoutesetMessageTO routesetMessageTO) {
        return new StringVirtualDepartureDateTrainId(String.valueOf(routesetMessageTO.getTrainNumber()), routesetMessageTO.getDepartureDate());
    }

    @Override
    public RoutesetMessageTO createChildTOFromTuple(final Tuple tuple) {
        return routesetMessageTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return RoutesetMessage.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.ROUTESET;
    }

    @Override
    public EntityPath getEntityTable() {
        return QRoutesetMessage.routesetMessage;
    }

    @Override
    public BooleanExpression createWhere(final List<StringVirtualDepartureDateTrainId> keys) {
        return TrainIdOptimizer.optimize(QRoutesetMessage.routesetMessage.trainId, keys);
    }
}
