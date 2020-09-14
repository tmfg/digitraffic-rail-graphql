package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.ThirdCategoryCode;
import fi.digitraffic.graphql.rail.model.ThirdCategoryCodeTO;

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
