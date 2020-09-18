package fi.digitraffic.graphql.rail.filters;

public abstract class BaseFilter<EntityTOType, EntityFilterTOType> {
    public abstract boolean isFiltered(EntityTOType value, EntityFilterTOType filter);

    public abstract Class<EntityFilterTOType> getFilterTOType();

    public abstract Class<EntityTOType> getEntityTOType();


}
