package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.CompositionFilterTO;
import fi.digitraffic.graphql.rail.model.CompositionTO;

@Component
public class CompositionFilter extends FilterWithChildren<CompositionTO, CompositionFilterTO> {
    public Class<CompositionFilterTO> getFilterTOType() {
        return CompositionFilterTO.class;
    }

    public Class<CompositionTO> getEntityTOType() {
        return CompositionTO.class;
    }
}
