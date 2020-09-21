package fi.digitraffic.graphql.rail.filters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.TrainFilterTO;
import fi.digitraffic.graphql.rail.model.TrainLocationFilterTO;
import graphql.schema.GraphQLInputType;

@Component
public class FilterRegistry {
    @Autowired
    private List<BaseFilter> baseFilters;

    private Map<Class, BaseFilter> registry = new HashMap<>();

    @PostConstruct
    public void setup() {
        for (BaseFilter baseFilter : baseFilters) {
            registry.put(baseFilter.getFilterTOType(), baseFilter);
        }
    }

    public BaseFilter getFilterFor(Class entityFilterTOClass) {
        BaseFilter baseFilter = registry.get(entityFilterTOClass);
        if (baseFilter == null) {
            throw new IllegalArgumentException("Could not find filter implementation for type " + entityFilterTOClass);
        }
        return baseFilter;
    }

    public BaseFilter getFilterFor(GraphQLInputType type) {
        String typeName = type.getName();

        if (typeName.equals("TrainFilter")) {
            return this.getFilterFor(TrainFilterTO.class);
        } else if (typeName.equals("TrainLocationFilter")) {
            return this.getFilterFor(TrainLocationFilterTO.class);
        }
        {
            throw new IllegalArgumentException("Could not find filter implementation for type " + typeName);
        }
    }
}
