package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.ThirdCategoryCode;
import graphqlscope.graphql.model.ThirdCategoryCodeTO;

@Component
public class ThirdCategoryCodeTOConverter {
    public ThirdCategoryCodeTO convert(ThirdCategoryCode entity) {
        return new ThirdCategoryCodeTO(
                entity.code,
                entity.name,
                entity.description,
                entity.id.intValue(),
                entity.validFrom,
                entity.validTo,
                entity.detailedCategoryCodeId.intValue()
        );
    }
}
