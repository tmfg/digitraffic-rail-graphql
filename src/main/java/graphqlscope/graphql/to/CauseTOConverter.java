package graphqlscope.graphql.to;

import org.springframework.stereotype.Component;

import graphqlscope.graphql.entities.Cause;
import graphqlscope.graphql.model.CauseTO;

@Component
public class CauseTOConverter {
    public CauseTO convert(Cause entity) {
        return new CauseTO(
                entity.timeTableRowId.attapId.intValue(),
                entity.timeTableRowId.trainNumber.intValue(),
                entity.timeTableRowId.departureDate,
                entity.id.intValue(),
                entity.categoryCodeId.intValue(),
                entity.detailedCategoryCodeId.intValue(),
                entity.thirdCategoryCodeId.intValue()
        );
    }
}
