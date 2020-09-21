package fi.digitraffic.graphql.rail.filters.primitive;

import java.util.Objects;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.BaseFilter;
import fi.digitraffic.graphql.rail.model.IntegerFilterTO;

@Component
public class IntegerFilter extends BaseFilter<Integer, IntegerFilterTO> {

    @Override
    public boolean isFiltered(Integer value, IntegerFilterTO filterTO) {
        if (filterTO.getEq() != null) {
            return !Objects.equals(value, filterTO.getEq());
        } else if (filterTO.getGt() != null) {
            if (value == null) {
                return true;
            } else if (value <= filterTO.getGt()) {
                return true;
            } else {
                return false;
            }
        } else if (filterTO.getLt() != null) {
            if (value == null) {
                return true;
            } else if (value >= filterTO.getLt()) {
                return true;
            } else {
                return false;
            }
        }

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
