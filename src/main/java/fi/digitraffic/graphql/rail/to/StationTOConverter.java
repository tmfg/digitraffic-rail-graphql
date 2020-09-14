package fi.digitraffic.graphql.rail.to;

import java.util.List;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Station;
import fi.digitraffic.graphql.rail.entities.StationTypeEnum;
import fi.digitraffic.graphql.rail.model.StationTO;
import fi.digitraffic.graphql.rail.model.StationTypeEnumTO;

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

    private StationTypeEnumTO parseStationType(StationTypeEnum type) {
        if (type == StationTypeEnum.STATION) {
            return StationTypeEnumTO.STATION;
        } else if (type == StationTypeEnum.STOPPING_POINT) {
            return StationTypeEnumTO.STOPPING_POINT;
        } else if (type == StationTypeEnum.TURNOUT_IN_THE_OPEN_LINE) {
            return StationTypeEnumTO.TURNOUT_IN_THE_OPEN_LINE;
        } else {
            throw new IllegalArgumentException("Could not parse station type: " + type);
        }
    }
}
