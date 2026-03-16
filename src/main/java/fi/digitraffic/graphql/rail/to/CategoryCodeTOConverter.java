package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.CategoryCode;
import fi.digitraffic.graphql.rail.entities.QCategoryCode;
import fi.digitraffic.graphql.rail.model.CategoryCodeTO;

@Component
public class CategoryCodeTOConverter extends BaseConverter<CategoryCodeTO> {
    @Override
    public CategoryCodeTO convert(Tuple tuple) {
        return new CategoryCodeTO(
                tuple.get(QCategoryCode.categoryCode.code),
                tuple.get(QCategoryCode.categoryCode.name),
                tuple.get(QCategoryCode.categoryCode.oid),
                tuple.get(QCategoryCode.categoryCode.validFrom),
                tuple.get(QCategoryCode.categoryCode.validTo)
        );
    }

    public CategoryCodeTO convertEntity(final CategoryCode entity) {
        return new CategoryCodeTO(
                entity.code,
                entity.name,
                entity.oid,
                entity.validFrom,
                entity.validTo
        );
    }
}
