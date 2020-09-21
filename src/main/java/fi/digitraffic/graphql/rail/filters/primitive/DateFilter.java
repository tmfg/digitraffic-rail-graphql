package fi.digitraffic.graphql.rail.filters.primitive;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.BaseFilter;
import fi.digitraffic.graphql.rail.model.DateFilterTO;

@Component
public class DateFilter extends BaseFilter<LocalDate, DateFilterTO> {
    @Override
    public boolean isFiltered(LocalDate value, DateFilterTO filterTO) {
        if (filterTO.getEq() != null) {
            return !Objects.equals(value, filterTO.getEq());
        } else if (filterTO.getGt() != null) {
            if (value == null) {
                return true;
            } else if (value.isBefore(filterTO.getGt()) || value.isEqual(filterTO.getGt())) {
                return true;
            } else {
                return false;
            }
        } else if (filterTO.getLt() != null) {
            if (value == null) {
                return true;
            } else if (value.isAfter(filterTO.getLt()) || value.isEqual(filterTO.getLt())) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    @Override
    public Class getFilterTOType() {
        return DateFilterTO.class;
    }

    @Override
    public Class getEntityTOType() {
        return LocalDate.class;
    }
}
