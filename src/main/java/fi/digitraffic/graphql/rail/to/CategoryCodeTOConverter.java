package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.CategoryCode;
import fi.digitraffic.graphql.rail.model.CategoryCodeTO;

@Component
public class CategoryCodeTOConverter {
    public CategoryCodeTO convert(CategoryCode entity) {
        return new CategoryCodeTO(
                entity.categoryCode, entity.categoryName, entity.id.intValue(), entity.validFrom, entity.validTo
        );
    }
}
