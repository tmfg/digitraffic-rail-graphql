package fi.digitraffic.graphql.rail.links;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QThirdCategoryCode;
import fi.digitraffic.graphql.rail.entities.ThirdCategoryCode;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.model.ThirdCategoryCodeTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.ThirdCategoryCodeTOConverter;

@Component
public class CauseToThirdCategoryCodeLink extends OneToOneLink<String, CauseTO, ThirdCategoryCode, ThirdCategoryCodeTO> {
    @Autowired
    private ThirdCategoryCodeTOConverter thirdCategoryCodeTOConverter;

    @Override
    public String getTypeName() {
        return "Cause";
    }

    @Override
    public String getFieldName() {
        return "thirdCategoryCode";
    }

    @Override
    public String createKeyFromParent(final CauseTO causeTO) {
        final String thirdCategoryCodeId = causeTO.getThirdCategoryCodeOid();
        return Objects.requireNonNullElse(thirdCategoryCodeId, "-");
    }

    @Override
    public String createKeyFromChild(final ThirdCategoryCodeTO thirdCategoryCodeTO) {
        return thirdCategoryCodeTO.getOid();
    }

    @Override
    public ThirdCategoryCodeTO createChildTOFromTuple(final Tuple tuple) {
        return thirdCategoryCodeTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return ThirdCategoryCode.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.THIRD_CATEGORY_CODE;
    }

    @Override
    public EntityPath getEntityTable() {
        return QThirdCategoryCode.thirdCategoryCode;
    }

    @Override
    public BooleanExpression createWhere(final List<String> keys) {
        return QThirdCategoryCode.thirdCategoryCode.oid.in(keys);
    }
}
