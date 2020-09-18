package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.OperatorFilterTO;
import fi.digitraffic.graphql.rail.model.OperatorTO;

@Component
public class OperatorFilter extends FilterWithChildren<OperatorTO, OperatorFilterTO> {
    public Class<OperatorFilterTO> getFilterTOType() {
        return OperatorFilterTO.class;
    }

    public Class<OperatorTO> getEntityTOType() {
        return OperatorTO.class;
    }
}
