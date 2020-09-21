package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.ThirdCategoryCodeFilterTO;
import fi.digitraffic.graphql.rail.model.ThirdCategoryCodeTO;

@Component
public class ThirdCategoryCodeFilter extends FilterWithChildren<ThirdCategoryCodeTO, ThirdCategoryCodeFilterTO> {
    public Class<ThirdCategoryCodeFilterTO> getFilterTOType() {
        return ThirdCategoryCodeFilterTO.class;
    }

    public Class<ThirdCategoryCodeTO> getEntityTOType() {
        return ThirdCategoryCodeTO.class;
    }
}
