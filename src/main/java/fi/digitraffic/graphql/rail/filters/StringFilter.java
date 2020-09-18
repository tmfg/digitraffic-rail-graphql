package fi.digitraffic.graphql.rail.filters;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.StringFilterTO;

@Component
public class StringFilter extends BaseFilter<String, StringFilterTO> {
    @Override
    public boolean isFiltered(String value, StringFilterTO filterTO) {
        if (value == null) {
            return false;
        }
        if (!value.equals(filterTO.getEq())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAutoconfigured() {
        return false;
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
