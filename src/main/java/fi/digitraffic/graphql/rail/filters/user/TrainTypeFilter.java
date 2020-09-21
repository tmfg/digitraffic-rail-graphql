package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.TrainTypeFilterTO;
import fi.digitraffic.graphql.rail.model.TrainTypeTO;

@Component
public class TrainTypeFilter extends FilterWithChildren<TrainTypeTO, TrainTypeFilterTO> {
    public Class<TrainTypeFilterTO> getFilterTOType() {
        return TrainTypeFilterTO.class;
    }

    public Class<TrainTypeTO> getEntityTOType() {
        return TrainTypeTO.class;
    }
}
