package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.RoutesetMessageFilterTO;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;

@Component
public class RoutesetMessageFilter extends FilterWithChildren<RoutesetMessageTO, RoutesetMessageFilterTO> {
    public Class<RoutesetMessageFilterTO> getFilterTOType() {
        return RoutesetMessageFilterTO.class;
    }

    public Class<RoutesetMessageTO> getEntityTOType() {
        return RoutesetMessageTO.class;
    }
}
