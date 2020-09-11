package graphqlscope.graphql.to;

import java.util.List;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Station;
import graphqlscope.graphql.entities.StationTypeEnum;
import graphqlscope.graphql.model.StationTO;
import graphqlscope.graphql.model.StationTypeEnumTO;

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
