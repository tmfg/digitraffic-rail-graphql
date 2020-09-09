package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.TrainCategory;
import graphqlscope.graphql.model.TrainCategoryTO;

@Component
public class TrainCategoryTOConverter {
    public TrainCategoryTO convert(TrainCategory entity) {
        return new TrainCategoryTO(
                entity.id.intValue(),
                entity.name
        );
    }
}
