package fi.digitraffic.graphql.rail.filters;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.OperatorFilterTO;
import fi.digitraffic.graphql.rail.model.OperatorTO;

@Component
public class OperatorFilter extends BaseFilter<OperatorTO, OperatorFilterTO> {
    public Class<OperatorFilterTO> getFilterTOType() {
        return OperatorFilterTO.class;
    }

    public Class<OperatorTO> getEntityTOType() {
        return OperatorTO.class;
    }
}
