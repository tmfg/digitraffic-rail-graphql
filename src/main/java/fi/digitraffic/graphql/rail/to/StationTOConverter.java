package fi.digitraffic.graphql.rail.to;

import java.util.List;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.entities.StationTypeEnum;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.StationTypeTO;

@Component
public class StationTOConverter {
    public StationTO convert(Station entity) {
        return new StationTO(
                entity.id.intValue(),
                entity.passengerTraffic,
                entity.countryCode,
                List.of(entity.longitude.floatValue(), entity.latitude.floatValue()),
                entity.name,
                entity.shortCode,
                entity.uicCode,
                parseStationType(entity.type)
        );
    }

    private StationTypeTO parseStationType(StationTypeEnum type) {
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
