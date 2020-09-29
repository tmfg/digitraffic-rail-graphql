package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QRouteset;
import fi.digitraffic.graphql.rail.entities.Routeset;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;

@Component
public class RoutesetMessageTOConverter {
    public RoutesetMessageTO convert(Routeset entity) {
        return new RoutesetMessageTO(
                entity.id.intValue(),
                entity.version.toString(),
                entity.messageTime,
                entity.trainId.trainNumber,
                entity.trainId.virtualDepartureDate,
                entity.routeType,
                entity.clientSystem,
                null, null
        );
    }

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
