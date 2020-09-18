package fi.digitraffic.graphql.rail.filters;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.OperatorFilterTO;
import fi.digitraffic.graphql.rail.model.OperatorTO;

@Component
public class OperatorFilter extends BaseFilter<OperatorTO, OperatorFilterTO> {

    public boolean isFiltered(OperatorTO entity, OperatorFilterTO filter) {
        if (this.isChildFiltered(entity.getName(), filter.getName())) return true;
        if (this.isChildFiltered(entity.getShortCode(), filter.getShortCode())) return true;
        if (this.isChildFiltered(entity.getUicCode(), filter.getUicCode())) return true;

        return false;
    }

    public Class<OperatorFilterTO> getFilterTOType() {
        return OperatorFilterTO.class;
    }

    public Class<OperatorTO> getEntityTOType() {
        return OperatorTO.class;
    }
}
