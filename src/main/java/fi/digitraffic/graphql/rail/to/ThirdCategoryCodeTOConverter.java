package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QThirdCategoryCode;
import fi.digitraffic.graphql.rail.model.ThirdCategoryCodeTO;

@Component
public class ThirdCategoryCodeTOConverter extends BaseConverter<ThirdCategoryCodeTO> {
    @Override
    public ThirdCategoryCodeTO convert(Tuple tuple) {
        return new ThirdCategoryCodeTO(
                tuple.get(QThirdCategoryCode.thirdCategoryCode.code),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.name),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.description),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.id).intValue(),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.validFrom),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.validTo),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.detailedCategoryCodeId).intValue()
        );
    }
}
