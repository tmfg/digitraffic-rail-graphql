package fi.digitraffic.graphql.rail.filters.primitive;

import java.util.Objects;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.BaseFilter;
import fi.digitraffic.graphql.rail.model.EnumFilterTO;

@Component
public class EnumFilter extends BaseFilter<Enum, EnumFilterTO> {

    @Override
    public boolean isFiltered(Enum value, EnumFilterTO filterTO) {
        return !Objects.equals(value.toString(), filterTO.getEq());
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
