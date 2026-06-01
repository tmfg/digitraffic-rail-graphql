package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Routesection;
import fi.digitraffic.graphql.rail.model.RoutesectionTO;
import jakarta.persistence.Tuple;

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

    /**
     * Converts a JPQL Tuple row to a RoutesectionTO.
     * Alias names must match the projection expression in RoutesetToRouteSectionsLink.
     */
    public RoutesectionTO convertProjection(final Tuple row) {
        return new RoutesectionTO(
                row.get("sectionId", String.class),
                row.get("commercialTrackId", String.class),
                row.get("stationCode", String.class),
                row.get("routesetId", Long.class).intValue(),
                null);                                                    // station
    }
}
