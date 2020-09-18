package fi.digitraffic.graphql.rail.filters;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseFilter<EntityTOType, EntityFilterTOType> {
    @Autowired
    private FilterRegistry filterRegistry;

    public abstract boolean isFiltered(EntityTOType entity, EntityFilterTOType filter);

    public abstract Class<EntityFilterTOType> getFilterTOType();

    public abstract Class<EntityTOType> getEntityTOType();

    protected boolean isChildFiltered(Object entity, Object filter) {
        if (filter != null) {
            boolean filtered = filterRegistry.getFilterFor(filter.getClass()).isFiltered(entity, filter);
            if (filtered) {
                return true;
            }
        }
        return false;
    }

    protected boolean filterByOr(Collection<EntityFilterTOType> filters, EntityTOType entity) {
        if (filters == null) {
            return false;
        }

        for (EntityFilterTOType filter : filters) {
            if (!isFiltered(entity, filter)) {
                return false;
            }
        }

        return true;
    }

    protected Boolean filterByAnd(Collection<EntityFilterTOType> filters, EntityTOType entity) {
        if (filters == null) {
            return false;
        }

        for (EntityFilterTOType filter : filters) {
            if (isFiltered(entity, filter)) {
                return true;
            }
        }

        return false;
    }
}
