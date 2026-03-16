package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.DetailedCategoryCode;
import fi.digitraffic.graphql.rail.entities.QDetailedCategoryCode;
import fi.digitraffic.graphql.rail.model.DetailedCategoryCodeTO;

@Component
public class DetailedCategoryCodeTOConverter extends BaseConverter<DetailedCategoryCodeTO> {
    @Override
    public DetailedCategoryCodeTO convert(Tuple tuple) {
        return new DetailedCategoryCodeTO(
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.code),
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.name),
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.oid),
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.categoryCodeOid),
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.validFrom),
                tuple.get(QDetailedCategoryCode.detailedCategoryCode.validTo)
        );
    }

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
