package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.config.graphql.AllFields;
import fi.digitraffic.graphql.rail.entities.DetailedCategoryCode;
import fi.digitraffic.graphql.rail.entities.QDetailedCategoryCode;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.model.DetailedCategoryCodeTO;
import fi.digitraffic.graphql.rail.to.DetailedCategoryCodeTOConverter;

@Component
public class CauseToDetailedCategoryCodeLink extends OneToOneLink<Long, CauseTO, DetailedCategoryCode, DetailedCategoryCodeTO> {
    @Autowired
    private DetailedCategoryCodeTOConverter detailedCategoryCodeTOConverter;

    @Override
    public String getTypeName() {
        return "Cause";
    }

    @Override
    public String getFieldName() {
        return "detailedCategoryCode";
    }

    @Override
    public Long createKeyFromParent(CauseTO causeTO) {
        Integer detailedCategoryCodeId = causeTO.getDetailedCategoryCodeId();
        if (detailedCategoryCodeId == null) {
            return -1L;
        } else {
            return detailedCategoryCodeId.longValue();
        }
    }

    @Override
    public Long createKeyFromChild(DetailedCategoryCodeTO detailedCategoryCodeTO) {
        return detailedCategoryCodeTO.getId().longValue();
    }

    @Override
    public DetailedCategoryCodeTO createChildTOFromTuple(Tuple tuple) {
        return detailedCategoryCodeTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return DetailedCategoryCode.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.DETAILED_CATEGORY_CODE;
    }

    @Override
    public EntityPath getEntityTable() {
        return QDetailedCategoryCode.detailedCategoryCode;
    }

    @Override
    public BooleanExpression createWhere(List<Long> keys) {
        return QDetailedCategoryCode.detailedCategoryCode.id.in(keys);
    }
}
