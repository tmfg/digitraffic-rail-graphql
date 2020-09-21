package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.DetailedCategoryCodeFilterTO;
import fi.digitraffic.graphql.rail.model.DetailedCategoryCodeTO;

@Component
public class DetailedCategoryCodeFilter extends FilterWithChildren<DetailedCategoryCodeTO, DetailedCategoryCodeFilterTO> {
    public Class<DetailedCategoryCodeFilterTO> getFilterTOType() {
        return DetailedCategoryCodeFilterTO.class;
    }

    public Class<DetailedCategoryCodeTO> getEntityTOType() {
        return DetailedCategoryCodeTO.class;
    }
}
