package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.CategoryCode;
import graphqlscope.graphql.model.CategoryCodeTO;

@Component
public class CategoryCodeTOConverter {
    public CategoryCodeTO convert(CategoryCode entity) {
        return new CategoryCodeTO(
                entity.categoryCode, entity.categoryName, entity.id.intValue(), entity.validFrom, entity.validTo
        );
    }
}
