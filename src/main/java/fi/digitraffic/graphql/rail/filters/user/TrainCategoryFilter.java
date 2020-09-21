package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.TrainCategoryFilterTO;
import fi.digitraffic.graphql.rail.model.TrainCategoryTO;

@Component
public class TrainCategoryFilter extends FilterWithChildren<TrainCategoryTO, TrainCategoryFilterTO> {
    public Class<TrainCategoryFilterTO> getFilterTOType() {
        return TrainCategoryFilterTO.class;
    }

    public Class<TrainCategoryTO> getEntityTOType() {
        return TrainCategoryTO.class;
    }
}
