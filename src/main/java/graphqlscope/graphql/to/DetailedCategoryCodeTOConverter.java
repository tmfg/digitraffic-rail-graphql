package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.DetailedCategoryCode;
import graphqlscope.graphql.model.DetailedCategoryCodeTO;

@Component
public class DetailedCategoryCodeTOConverter {
    public DetailedCategoryCodeTO convert(DetailedCategoryCode entity) {
        return new DetailedCategoryCodeTO(
                entity.detailedCategoryCode, entity.detailedCategoryName, entity.id.intValue(), entity.categoryCodeId.intValue(), entity.validFrom, entity.validTo
        );
    }
}
