package fi.digitraffic.graphql.rail.filters.primitive;

import java.util.Objects;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.BaseFilter;
import fi.digitraffic.graphql.rail.model.BooleanFilterTO;

@Component
public class BooleanFilter extends BaseFilter<Boolean, BooleanFilterTO> {

    @Override
    public boolean isFiltered(Boolean value, BooleanFilterTO filterTO) {
        return !Objects.equals(value, filterTO.getEq());
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
