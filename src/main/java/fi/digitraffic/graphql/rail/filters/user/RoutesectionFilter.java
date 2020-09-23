package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.RoutesectionFilterTO;
import fi.digitraffic.graphql.rail.model.RoutesectionTO;

@Component
public class RoutesectionFilter extends FilterWithChildren<RoutesectionTO, RoutesectionFilterTO> {
    public Class<RoutesectionFilterTO> getFilterTOType() {
        return RoutesectionFilterTO.class;
    }

    public Class<RoutesectionTO> getEntityTOType() {
        return RoutesectionTO.class;
    }
}

