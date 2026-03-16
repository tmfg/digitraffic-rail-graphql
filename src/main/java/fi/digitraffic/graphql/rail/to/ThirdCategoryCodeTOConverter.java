package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QThirdCategoryCode;
import fi.digitraffic.graphql.rail.entities.ThirdCategoryCode;
import fi.digitraffic.graphql.rail.model.ThirdCategoryCodeTO;

@Component
public class ThirdCategoryCodeTOConverter extends BaseConverter<ThirdCategoryCodeTO> {
    @Override
    public ThirdCategoryCodeTO convert(Tuple tuple) {
        return new ThirdCategoryCodeTO(
                tuple.get(QThirdCategoryCode.thirdCategoryCode.code),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.name),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.description),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.oid),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.validFrom),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.validTo),
                tuple.get(QThirdCategoryCode.thirdCategoryCode.detailedCategoryCodeOid)
        );
    }

    public ThirdCategoryCodeTO convertEntity(final ThirdCategoryCode entity) {
        return new ThirdCategoryCodeTO(
                entity.code,
                entity.name,
                entity.description,
                entity.oid,
                entity.validFrom,
                entity.validTo,
                entity.detailedCategoryCodeOid
        );
    }
}
