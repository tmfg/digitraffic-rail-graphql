package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainType;
import fi.digitraffic.graphql.rail.model.TrainTypeTO;

@Component
public class TrainTypeTOConverter {
    public TrainTypeTO convert(TrainType entity) {
        return new TrainTypeTO(
                entity.id.intValue(),
                entity.name,
                entity.trainCategoryId.intValue(), null
        );
    }
}
