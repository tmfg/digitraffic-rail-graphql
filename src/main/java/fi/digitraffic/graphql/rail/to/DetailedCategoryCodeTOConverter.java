package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.DetailedCategoryCode;
import fi.digitraffic.graphql.rail.model.DetailedCategoryCodeTO;

@Component
public class DetailedCategoryCodeTOConverter {
    public DetailedCategoryCodeTO convert(DetailedCategoryCode entity) {
        return new DetailedCategoryCodeTO(
                entity.detailedCategoryCode, entity.detailedCategoryName, entity.id.intValue(), entity.categoryCodeId.intValue(), entity.validFrom, entity.validTo
        );
    }
}
