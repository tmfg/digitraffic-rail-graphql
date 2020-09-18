package fi.digitraffic.graphql.rail.filters;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.IntegerFilterTO;

@Component
public class IntegerFilter extends BaseFilter<Integer, IntegerFilterTO> {

    @Override
    public boolean isFiltered(Integer value, IntegerFilterTO filterTO) {
        if (value == null) {
            return false;
        }
        if (filterTO.getEq() != null) {
            if (!value.equals(filterTO.getEq())) {
                return true;
            } else {
                return false;
            }
        } else if (filterTO.getGt() != null) {
            if (value <= filterTO.getGt()) {
                return true;
            } else {
                return false;
            }
        } else if (filterTO.getLt() != null) {
            if (value >= filterTO.getLt()) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean isAutoconfigured() {
        return false;
    }

    @Override
    public Class getFilterTOType() {
        return IntegerFilterTO.class;
    }

    @Override
    public Class getEntityTOType() {
        return Integer.class;
    }
}
