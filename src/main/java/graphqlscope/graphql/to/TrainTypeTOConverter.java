package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.TrainType;
import graphqlscope.graphql.model.TrainTypeTO;

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
