package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.DetailedCategoryCode;
import fi.digitraffic.graphql.rail.model.DetailedCategoryCodeTO;

@Component
public class DetailedCategoryCodeTOConverter extends BaseConverter {

    public DetailedCategoryCodeTO convertEntity(final DetailedCategoryCode entity) {
        return new DetailedCategoryCodeTO(
                entity.code,
                entity.name,
                entity.oid,
                entity.categoryCodeOid,
                entity.validFrom,
                entity.validTo
        );
    }
}
