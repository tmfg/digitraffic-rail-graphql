package fi.digitraffic.graphql.rail.filters.user;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.filters.FilterWithChildren;
import fi.digitraffic.graphql.rail.model.TrainLocationFilterTO;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;

@Component
public class TrainLocationFilter extends FilterWithChildren<TrainLocationTO, TrainLocationFilterTO> {
    public Class<TrainLocationFilterTO> getFilterTOType() {
        return TrainLocationFilterTO.class;
    }

    public Class<TrainLocationTO> getEntityTOType() {
        return TrainLocationTO.class;
    }
}
