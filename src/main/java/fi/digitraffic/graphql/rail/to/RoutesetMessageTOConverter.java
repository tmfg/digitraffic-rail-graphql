package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.RoutesetMessage;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;

@Component
public class RoutesetMessageTOConverter extends BaseConverter {

    public RoutesetMessageTO convertEntity(final RoutesetMessage entity) {
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
}
