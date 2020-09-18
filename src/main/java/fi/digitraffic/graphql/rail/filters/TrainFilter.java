package fi.digitraffic.graphql.rail.filters;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.TrainFilterTO;
import fi.digitraffic.graphql.rail.model.TrainTO;

@Component
public class TrainFilter extends BaseFilter<TrainTO, TrainFilterTO> {
    public Class<TrainFilterTO> getFilterTOType() {
        return TrainFilterTO.class;
    }

    public Class<TrainTO> getEntityTOType() {
        return TrainTO.class;
    }
}
