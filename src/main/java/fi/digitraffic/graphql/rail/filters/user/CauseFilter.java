package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.CauseFilterTO;
import fi.digitraffic.graphql.rail.model.CauseTO;

@Component
public class CauseFilter extends FilterWithChildren<CauseTO, CauseFilterTO> {
    public Class<CauseFilterTO> getFilterTOType() {
        return CauseFilterTO.class;
    }

    public Class<CauseTO> getEntityTOType() {
        return CauseTO.class;
    }
}
