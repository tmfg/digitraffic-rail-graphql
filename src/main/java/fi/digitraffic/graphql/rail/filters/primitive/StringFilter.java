package fi.digitraffic.graphql.rail.filters.primitive;

import java.util.Objects;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.BaseFilter;
import fi.digitraffic.graphql.rail.model.StringFilterTO;

@Component
public class StringFilter extends BaseFilter<String, StringFilterTO> {
    @Override
    public boolean isFiltered(String value, StringFilterTO filterTO) {
        return !Objects.equals(value, filterTO.getEq());
    }

    @Override
    public Class getFilterTOType() {
        return StringFilterTO.class;
    }

    @Override
    public Class getEntityTOType() {
        return String.class;
    }
}
