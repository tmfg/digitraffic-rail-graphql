package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainCategory;
import fi.digitraffic.graphql.rail.model.TrainCategoryTO;

@Component
public class TrainCategoryTOConverter extends BaseConverter {

    public TrainCategoryTO convertEntity(final TrainCategory entity) {
        return new TrainCategoryTO(
                entity.id.intValue(),
                entity.name
        );
    }
}
