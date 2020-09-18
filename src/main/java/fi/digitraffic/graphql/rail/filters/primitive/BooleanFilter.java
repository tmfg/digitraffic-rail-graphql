package fi.digitraffic.graphql.rail.filters.primitive;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.BaseFilter;
import fi.digitraffic.graphql.rail.model.BooleanFilterTO;

@Component
public class BooleanFilter extends BaseFilter<Boolean, BooleanFilterTO> {

    @Override
    public boolean isFiltered(Boolean aBoolean, BooleanFilterTO booleanFilterTO) {
        if (aBoolean == null) {
            return false;
        }
        if (aBoolean != booleanFilterTO.getEq()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Class getFilterTOType() {
        return BooleanFilterTO.class;
    }

    @Override
    public Class getEntityTOType() {
        return Boolean.class;
    }
}
