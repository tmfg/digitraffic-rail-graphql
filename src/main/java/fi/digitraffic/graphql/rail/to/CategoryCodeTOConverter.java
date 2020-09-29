package fi.digitraffic.graphql.rail.to;

import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import fi.digitraffic.graphql.rail.entities.QCategoryCode;
import fi.digitraffic.graphql.rail.model.CategoryCodeTO;

@Component
public class CategoryCodeTOConverter extends BaseConverter<CategoryCodeTO> {
    @Override
    public CategoryCodeTO convert(Tuple tuple) {
        return new CategoryCodeTO(
                tuple.get(QCategoryCode.categoryCode.code),
                tuple.get(QCategoryCode.categoryCode.name),
                tuple.get(QCategoryCode.categoryCode.id).intValue(),
                tuple.get(QCategoryCode.categoryCode.validFrom),
                tuple.get(QCategoryCode.categoryCode.validTo)
        );
    }
}
