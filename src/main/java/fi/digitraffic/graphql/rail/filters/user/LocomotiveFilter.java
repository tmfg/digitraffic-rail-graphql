package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.LocomotiveFilterTO;
import fi.digitraffic.graphql.rail.model.LocomotiveTO;

@Component
public class LocomotiveFilter extends FilterWithChildren<LocomotiveTO, LocomotiveFilterTO> {
    public Class<LocomotiveFilterTO> getFilterTOType() {
        return LocomotiveFilterTO.class;
    }

    public Class<LocomotiveTO> getEntityTOType() {
        return LocomotiveTO.class;
    }
}
