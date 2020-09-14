package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Cause;
import fi.digitraffic.graphql.rail.model.CauseTO;

@Component
public class CauseTOConverter {
    public CauseTO convert(Cause entity) {
        return new CauseTO(
                entity.timeTableRowId.attapId.intValue(),
                entity.timeTableRowId.trainNumber.intValue(),
                entity.timeTableRowId.departureDate,
                entity.id.intValue(),
                longToNullableInteger(entity.categoryCodeId),
                longToNullableInteger(entity.detailedCategoryCodeId),
                longToNullableInteger(entity.thirdCategoryCodeId),
                null,
                null,
                null
        );
    }

    public Integer longToNullableInteger(Long value) {
        if (value == null) {
            return null;
        } else {
            return value.intValue();
        }
    }
}
