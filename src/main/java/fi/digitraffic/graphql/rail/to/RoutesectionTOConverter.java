package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QRoutesection;
import fi.digitraffic.graphql.rail.entities.Routesection;
import fi.digitraffic.graphql.rail.model.RoutesectionTO;

@Component
public class RoutesectionTOConverter extends BaseConverter<RoutesectionTO> {
    @Override
    public RoutesectionTO convert(final Tuple tuple) {
        return new RoutesectionTO(
                tuple.get(QRoutesection.routesection.sectionId),
                tuple.get(QRoutesection.routesection.commercialTrackId),
                tuple.get(QRoutesection.routesection.stationCode),
                tuple.get(QRoutesection.routesection.routesetId).intValue(),
                null
        );
    }

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
