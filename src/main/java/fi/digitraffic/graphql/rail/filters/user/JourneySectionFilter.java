package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.JourneySectionFilterTO;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;

@Component
public class JourneySectionFilter extends FilterWithChildren<JourneySectionTO, JourneySectionFilterTO> {
    public Class<JourneySectionFilterTO> getFilterTOType() {
        return JourneySectionFilterTO.class;
    }

    public Class<JourneySectionTO> getEntityTOType() {
        return JourneySectionTO.class;
    }
}
