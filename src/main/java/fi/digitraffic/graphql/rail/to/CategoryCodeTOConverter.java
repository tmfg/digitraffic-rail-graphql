package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.CategoryCode;
import fi.digitraffic.graphql.rail.model.CategoryCodeTO;

@Component
public class CategoryCodeTOConverter extends BaseConverter {
    public CategoryCodeTO convertEntity(final CategoryCode entity) {
        return new CategoryCodeTO(
                entity.code,
                entity.name,
                entity.oid,
                entity.validFrom,
                entity.validTo
        );
    }
}
