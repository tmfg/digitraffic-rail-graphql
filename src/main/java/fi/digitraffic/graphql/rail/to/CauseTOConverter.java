package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Cause;
import fi.digitraffic.graphql.rail.model.CauseTO;

@Component
public class CauseTOConverter extends BaseConverter {

    public CauseTO convertEntity(final Cause entity) {
        return new CauseTO(
                entity.timeTableRowId.attapId.intValue(),
                entity.timeTableRowId.trainNumber.intValue(),
                entity.timeTableRowId.departureDate,
                entity.id.intValue(),
                entity.categoryCodeOid,
                entity.detailedCategoryCodeOid,
                entity.thirdCategoryCodeOid,
                null,
                null,
                null
        );
    }
}
