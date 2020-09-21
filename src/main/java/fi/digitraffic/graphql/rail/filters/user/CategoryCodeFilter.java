package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.CategoryCodeFilterTO;
import fi.digitraffic.graphql.rail.model.CategoryCodeTO;

@Component
public class CategoryCodeFilter extends FilterWithChildren<CategoryCodeTO, CategoryCodeFilterTO> {
    public Class<CategoryCodeFilterTO> getFilterTOType() {
        return CategoryCodeFilterTO.class;
    }

    public Class<CategoryCodeTO> getEntityTOType() {
        return CategoryCodeTO.class;
    }
}
