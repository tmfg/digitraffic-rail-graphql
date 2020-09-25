package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.WagonFilterTO;
import fi.digitraffic.graphql.rail.model.WagonTO;

@Component
public class WagonFilter extends FilterWithChildren<WagonTO, WagonFilterTO> {
    public Class<WagonFilterTO> getFilterTOType() {
        return WagonFilterTO.class;
    }

    public Class<WagonTO> getEntityTOType() {
        return WagonTO.class;
    }
}
