package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Routesection;
import fi.digitraffic.graphql.rail.model.RoutesectionTO;

@Component
public class RoutesectionTOConverter extends BaseConverter {

    public RoutesectionTO convertEntity(final Routesection entity) {
        return new RoutesectionTO(
                entity.sectionId,
                entity.commercialTrackId,
                entity.stationCode,
                entity.routesetId.intValue(),
                null
        );
    }
}
