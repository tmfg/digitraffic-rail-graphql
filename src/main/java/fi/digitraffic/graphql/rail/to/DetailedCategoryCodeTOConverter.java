package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QDetailedCategoryCode;
import fi.digitraffic.graphql.rail.model.DetailedCategoryCodeTO;

@Component
public class DetailedCategoryCodeTOConverter extends BaseConverter<DetailedCategoryCodeTO> {
    @Override
    public DetailedCategoryCodeTO convert(Tuple tuple) {
        return new DetailedCategoryCodeTO(
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.code),
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.name),
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.id).intValue(),
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.categoryCodeId).intValue(),
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.validFrom),
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.validTo)
        );
    }
}
