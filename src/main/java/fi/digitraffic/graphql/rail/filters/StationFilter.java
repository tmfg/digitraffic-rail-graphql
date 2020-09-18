package fi.digitraffic.graphql.rail.filters;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.StationFilterTO;
import fi.digitraffic.graphql.rail.model.StationTO;

@Component
public class StationFilter extends FilterWithChildren<StationTO, StationFilterTO> {
    public Class<StationFilterTO> getFilterTOType() {
        return StationFilterTO.class;
    }

    public Class<StationTO> getEntityTOType() {
        return StationTO.class;
    }
}
