package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QRouteset;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;

@Component
public class RoutesetMessageTOConverter extends BaseConverter<RoutesetMessageTO> {
    public RoutesetMessageTO convert(Tuple tuple) {
        return new RoutesetMessageTO(
                tuple.get(QRouteset.routeset.id).intValue(),
                tuple.get(QRouteset.routeset.version).toString(),
                tuple.get(QRouteset.routeset.messageTime),
                tuple.get(QRouteset.routeset.trainId.trainNumber),
                tuple.get(QRouteset.routeset.trainId.virtualDepartureDate),
                tuple.get(QRouteset.routeset.routeType),
                tuple.get(QRouteset.routeset.clientSystem),
                null, null
        );
    }
}
