package fi.digitraffic.graphql.rail.filters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterRegistry {
    @Autowired
    private List<BaseFilter> baseFilters;

    private Map<String, BaseFilter> registry = new HashMap<>();

    @PostConstruct
    public void setup() {
        for (BaseFilter baseFilter : baseFilters) {
            registry.put(baseFilter.getFilterTOType().getSimpleName(), baseFilter);
        }
    }

    public BaseFilter getFilterFor(String filterName) {
        BaseFilter baseFilter = registry.get(filterName);
        if (baseFilter == null) {
            throw new IllegalArgumentException("Could not find filter implementation for type " + filterName);
        }
        return baseFilter;
    }

//    public BaseFilter getFilterFor(Class entityFilterTOClass) {
//        BaseFilter baseFilter = registry.get(entityFilterTOClass);
//        if (baseFilter == null) {
//            throw new IllegalArgumentException("Could not find filter implementation for type " + entityFilterTOClass);
//        }
//        return baseFilter;
//    }
//
//    public BaseFilter getFilterFor(GraphQLInputType type) {
//        String typeName = type.getName();
//
//        if (typeName.equals("TrainFilter")) {
//            return this.getFilterFor(TrainFilterTO.class);
//        } else if (typeName.equals("TrainLocationFilter")) {
//            return this.getFilterFor(TrainLocationFilterTO.class);
//        } else if (typeName.equals("RoutesetMessageFilter")) {
//            return this.getFilterFor(RoutesetMessageFilterTO.class);
//        } else if (typeName.equals("TrainTrackingMessageFilterFilter")) {
//            return this.getFilterFor(TrainTrackingMessageFilterTO.class);
//        } else if (typeName.equals("CompositionFilter")) {
//            return this.getFilterFor(CompositionFilterTO.class);
//        } else {
//            throw new IllegalArgumentException("Could not find filter implementation for type " + typeName);
//        }
//    }
}
