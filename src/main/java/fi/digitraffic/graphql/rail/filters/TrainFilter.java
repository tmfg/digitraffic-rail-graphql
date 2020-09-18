package fi.digitraffic.graphql.rail.filters;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.TrainFilterTO;
import fi.digitraffic.graphql.rail.model.TrainTO;

@Component
public class TrainFilter extends BaseFilter<TrainTO, TrainFilterTO> {

    public boolean isFiltered(TrainTO entity, TrainFilterTO filter) {
        if (this.isChildFiltered(entity.getCancelled(), filter.getCancelled())) return true;
        if (this.isChildFiltered(entity.getDeleted(), filter.getDeleted())) return true;
        if (this.isChildFiltered(entity.getRunningCurrently(), filter.getRunningCurrently())) return true;
        if (this.isChildFiltered(entity.getCommuterLineid(), filter.getCommuterLineid())) return true;
        if (this.filterByAND(filter.getAnd(), entity)) return true;
        if (this.filterByOR(filter.getOr(), entity)) return true;

        return false;
    }

    public Class<TrainFilterTO> getFilterTOType() {
        return TrainFilterTO.class;
    }

    public Class<TrainTO> getEntityTOType() {
        return TrainTO.class;
    }
}
