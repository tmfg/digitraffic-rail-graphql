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
                tuple.get(QStation.station.id).intValue(),
                falseIfNull(tuple.get(QStation.station.passengerTraffic)),
                tuple.get(QStation.station.countryCode),
                List.of(zeroIfNull(tuple.get(QStation.station.longitude)), zeroIfNull(tuple.get(QStation.station.latitude))),
                tuple.get(QStation.station.name),
                tuple.get(QStation.station.shortCode),
                zeroIfNull(tuple.get(QStation.station.uicCode)),
                parseStationType(tuple.get(QStation.station.type)),
                null
        );
    }

    private StationTypeTO parseStationType(final StationTypeEnum type) {
        if (type == null) {
            return null;
        }

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
