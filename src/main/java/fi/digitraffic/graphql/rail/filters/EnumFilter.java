package fi.digitraffic.graphql.rail.filters;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.EnumFilterTO;

@Component
public class EnumFilter extends BaseFilter<Enum, EnumFilterTO> {

    @Override
    public boolean isFiltered(Enum value, EnumFilterTO filterTO) {
        if (value == null) {
            return false;
        }
        if (filterTO.getEq() != null) {
            if (!value.name().equals(filterTO.getEq())) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    @Override
    public Class getFilterTOType() {
        return EnumFilterTO.class;
    }

    @Override
    public Class getEntityTOType() {
        return Enum.class;
    }
}
