package fi.digitraffic.graphql.rail.filters;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.StationFilterTO;
import fi.digitraffic.graphql.rail.model.StationTO;

@Component
public class StationFilter extends BaseFilter<StationTO, StationFilterTO> {

    public boolean isFiltered(StationTO entity, StationFilterTO filter) {
        if (this.isChildFiltered(entity.getCountryCode(), filter.getCountryCode())) return true;
        if (this.isChildFiltered(entity.getName(), filter.getName())) return true;
        if (this.isChildFiltered(entity.getPassengerTraffic(), filter.getPassengerTraffic())) return true;
        if (this.isChildFiltered(entity.getShortCode(), filter.getShortCode())) return true;
        if (this.isChildFiltered(entity.getType(), filter.getType())) return true;
        if (this.isChildFiltered(entity.getUicCode(), filter.getUicCode())) return true;

        return false;
    }

    public Class<StationFilterTO> getFilterTOType() {
        return StationFilterTO.class;
    }

    public Class<StationTO> getEntityTOType() {
        return StationTO.class;
    }
}
