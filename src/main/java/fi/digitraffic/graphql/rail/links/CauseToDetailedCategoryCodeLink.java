package fi.digitraffic.graphql.rail.links;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.DetailedCategoryCode;
import fi.digitraffic.graphql.rail.entities.QDetailedCategoryCode;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.model.DetailedCategoryCodeTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.DetailedCategoryCodeTOConverter;

@Component
public class CauseToDetailedCategoryCodeLink extends OneToOneLink<String, CauseTO, DetailedCategoryCode, DetailedCategoryCodeTO> {
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
    public String createKeyFromParent(final CauseTO causeTO) {
        final String detailedCategoryCodeId = causeTO.getDetailedCategoryCodeOid();
        return Objects.requireNonNullElse(detailedCategoryCodeId, "-");
    }

    @Override
    public String createKeyFromChild(final DetailedCategoryCodeTO detailedCategoryCodeTO) {
        return detailedCategoryCodeTO.getOid();
    }

    @Override
    public DetailedCategoryCodeTO createChildTOFromTuple(final Tuple tuple) {
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
    public BooleanExpression createWhere(final List<String> keys) {
        return QDetailedCategoryCode.detailedCategoryCode.oid.in(keys);
    }
}
