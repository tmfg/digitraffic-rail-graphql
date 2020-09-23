package fi.digitraffic.graphql.rail.filters.primitive;

import java.util.Objects;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.BaseFilter;
import fi.digitraffic.graphql.rail.model.VersionFilterTO;

@Component
public class VersionFilter extends BaseFilter<String, VersionFilterTO> {

    @Override
    public boolean isFiltered(String value, VersionFilterTO filterTO) {
        if (filterTO.getEq() != null) {
            return !Objects.equals(value, filterTO.getEq());
        } else if (filterTO.getGt() != null) {
            if (value == null) {
                return true;
            } else if (Long.parseLong(value) <= Long.parseLong(filterTO.getGt())) {
                return true;
            } else {
                return false;
            }
        } else if (filterTO.getLt() != null) {
            if (value == null) {
                return true;
            } else if (Long.parseLong(value) >= Long.parseLong(filterTO.getLt())) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    @Override
    public Class getFilterTOType() {
        return VersionFilterTO.class;
    }

    @Override
    public Class getEntityTOType() {
        return String.class;
    }
}
