package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QRoutesection;
import fi.digitraffic.graphql.rail.model.RoutesectionTO;

@Component
public class RoutesectionTOConverter extends BaseConverter<RoutesectionTO> {
    @Override
    public RoutesectionTO convert(Tuple tuple) {
        return new RoutesectionTO(
                tuple.get(QRoutesection.routesection.sectionId),
                tuple.get(QRoutesection.routesection.commercialTrackId),
                tuple.get(QRoutesection.routesection.stationCode),
                tuple.get(QRoutesection.routesection.routesetId).intValue(),
                null
        );
    }
}
