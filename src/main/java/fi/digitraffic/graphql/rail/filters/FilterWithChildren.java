package fi.digitraffic.graphql.rail.filters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fi.digitraffic.graphql.rail.model.CoordinateFilterTO;

public abstract class FilterWithChildren<EntityTOType, EntityFilterTOType> extends BaseFilter<EntityTOType, EntityFilterTOType> {
    private List<List<Method>> fields = new ArrayList<>();
    private List<List<Method>> collections = new ArrayList<>();
    private Method and = null;
    private Method or = null;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FilterRegistry filterRegistry;

    @PostConstruct
    public void setup() throws NoSuchMethodException {
        Class<EntityFilterTOType> filterTOType = this.getFilterTOType();
        Class<EntityTOType> entityTOType = this.getEntityTOType();

        List<Method> getMethods = List.of(filterTOType.getDeclaredMethods()).stream().filter(s -> s.getName().startsWith("get")).collect(Collectors.toList());
        for (Method getMethod : getMethods) {
            String methodName = getMethod.getName();
            if (methodName.equals("getAnd")) {
                this.and = getMethod;
            } else if (methodName.equals("getOr")) {
                this.or = getMethod;
            } else if (getMethod.getReturnType().equals(CoordinateFilterTO.class)) {
                fields.add(List.of(entityTOType.getDeclaredMethod(methodName), filterTOType.getDeclaredMethod(methodName)));
            } else if (entityTOType.getDeclaredMethod(methodName).getReturnType().equals(Collection.class)) {
                collections.add(List.of(entityTOType.getDeclaredMethod(methodName), filterTOType.getDeclaredMethod(methodName)));
            } else {
                fields.add(List.of(entityTOType.getDeclaredMethod(methodName), filterTOType.getDeclaredMethod(methodName)));
            }
        }
    }

    public boolean isFiltered(EntityTOType entity, EntityFilterTOType filter) {
        try {
            if (this.and != null && this.filterByAnd((Collection<EntityFilterTOType>) this.and.invoke(filter), entity))
                return true;
            if (this.or != null && this.filterByOr((Collection<EntityFilterTOType>) this.or.invoke(filter), entity))
                return true;

            for (List<Method> methods : fields) {
                Object entityFieldValue = methods.get(0).invoke(entity);
                Object filterFieldValue = methods.get(1).invoke(filter);
                if (this.isChildFiltered(entityFieldValue, filterFieldValue)) return true;
            }

            for (List<Method> methods : collections) {
                Collection entityFieldValue = (Collection) methods.get(0).invoke(entity);
                Object filterFieldValue = methods.get(1).invoke(filter);
                if (this.filterCollection(entityFieldValue, filterFieldValue)) return true;
            }
        } catch (Exception e) {
            log.error("Error filtering", e);
        }

        return false;
    }

    private Boolean filterCollection(Collection entities, Object timeTableRowCollectionFilter) {
        if (timeTableRowCollectionFilter != null) {
            for (Object entity : entities) {
                boolean result = isChildFiltered(entity, timeTableRowCollectionFilter);

                if (!result) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    private boolean isChildFiltered(Object entity, Object filter) {
        if (filter != null) {
            boolean filtered = filterRegistry.getFilterFor(filter.getClass()).isFiltered(entity, filter);
            if (filtered) {
                return true;
            }
        }
        return false;
    }

    private boolean filterByOr(Collection<EntityFilterTOType> filters, EntityTOType entity) {
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

    private Boolean filterByAnd(Collection<EntityFilterTOType> filters, EntityTOType entity) {
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
