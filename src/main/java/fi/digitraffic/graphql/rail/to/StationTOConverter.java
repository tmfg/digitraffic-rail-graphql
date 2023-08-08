package fi.digitraffic.graphql.rail.to;

import java.util.List;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QStation;
import fi.digitraffic.graphql.rail.entities.StationTypeEnum;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.StationTypeTO;

@Component
public class StationTOConverter extends BaseConverter<StationTO> {
    @Override
    public StationTO convert(final Tuple tuple) {
        return new StationTO(
                tuple.get(QStation.station.id),
                tuple.get(QStation.station.passengerTraffic),
                tuple.get(QStation.station.countryCode),
                List.of(tuple.get(QStation.station.longitude).doubleValue(), tuple.get(QStation.station.latitude).doubleValue()),
                tuple.get(QStation.station.name),
                tuple.get(QStation.station.shortCode),
                tuple.get(QStation.station.uicCode),
                parseStationType(tuple.get(QStation.station.type)),
                null
        );
    }

    private StationTypeTO parseStationType(final StationTypeEnum type) {
        if (type == StationTypeEnum.STATION) {
            return StationTypeTO.STATION;
        } else if (type == StationTypeEnum.STOPPING_POINT) {
            return StationTypeTO.STOPPING_POINT;
        } else if (type == StationTypeEnum.TURNOUT_IN_THE_OPEN_LINE) {
            return StationTypeTO.TURNOUT_IN_THE_OPEN_LINE;
        } else {
            throw new IllegalArgumentException("Could not parse station type: " + type);
        }
    }
}
