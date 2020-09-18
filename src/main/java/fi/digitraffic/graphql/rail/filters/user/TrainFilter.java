package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.TrainFilterTO;
import fi.digitraffic.graphql.rail.model.TrainTO;

@Component
public class TrainFilter extends FilterWithChildren<TrainTO, TrainFilterTO> {
    public Class<TrainFilterTO> getFilterTOType() {
        return TrainFilterTO.class;
    }

    public Class<TrainTO> getEntityTOType() {
        return TrainTO.class;
    }
}
