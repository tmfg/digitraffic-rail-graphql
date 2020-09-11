package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Composition;
import graphqlscope.graphql.model.CompositionTO;

@Component
public class CompositionTOConverter {
    public CompositionTO convert(Composition entity) {
        return new CompositionTO(
                entity.id.departureDate,
                entity.id.trainNumber.intValue(),
                entity.operatorShortCode,
                entity.trainCategoryId.intValue(),
                entity.trainTypeId.intValue(),
                entity.version.toString(),
                entity.operatorUicCode,
                null,
                null
        );
    }
}
