package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QRoutesetMessage;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;

@Component
public class RoutesetMessageTOConverter extends BaseConverter<RoutesetMessageTO> {
    public RoutesetMessageTO convert(final Tuple tuple) {
        return new RoutesetMessageTO(
                tuple.get(QRoutesetMessage.routesetMessage.id).intValue(),
                tuple.get(QRoutesetMessage.routesetMessage.version).toString(),
                tuple.get(QRoutesetMessage.routesetMessage.messageTime),
                tuple.get(QRoutesetMessage.routesetMessage.trainId.trainNumber),
                tuple.get(QRoutesetMessage.routesetMessage.trainId.virtualDepartureDate),
                tuple.get(QRoutesetMessage.routesetMessage.routeType),
                tuple.get(QRoutesetMessage.routesetMessage.clientSystem),
                null, null
        );
    }
}
