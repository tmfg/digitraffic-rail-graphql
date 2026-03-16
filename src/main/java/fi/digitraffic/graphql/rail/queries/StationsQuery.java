package fi.digitraffic.graphql.rail.queries;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.entities.StationTypeEnum;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.StationTypeTO;
import graphql.schema.DataFetchingEnvironment;

@Component
public class StationsQuery extends BaseQuery<Station, StationTO> {

    public StationsQuery(final JpqlWhereBuilder whereBuilder,
                         final JpqlOrderByBuilder orderByBuilder,
                         @Value("${digitraffic.max-returned-rows}") final int maxResults) {
        super(whereBuilder, orderByBuilder, maxResults);
    }

    @Override
    public String getQueryName() {
        return "stations";
    }

    @Override
    public Class<Station> getEntityClass() {
        return Station.class;
    }


    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                        final Map<String, Object> parameters) {
        return alias + ".id <> -1";
    }

    @Override
    public String getDefaultOrderBy(final String alias) {
        return alias + ".name ASC";
    }

    @Override
    public StationTO convertEntityToTO(final Station entity) {
        return new StationTO(
                entity.id.intValue(),
                entity.passengerTraffic,
                entity.countryCode,
                List.of(entity.longitude, entity.latitude),
                entity.name,
                entity.shortCode,
                entity.uicCode,
                parseStationType(entity.type),
                null, // timeTableRows - populated by link
                null  // stationMessages - populated by link
        );
    }

    private StationTypeTO parseStationType(final StationTypeEnum type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case STATION -> StationTypeTO.STATION;
            case STOPPING_POINT -> StationTypeTO.STOPPING_POINT;
            case TURNOUT_IN_THE_OPEN_LINE -> StationTypeTO.TURNOUT_IN_THE_OPEN_LINE;
        };
    }
}

