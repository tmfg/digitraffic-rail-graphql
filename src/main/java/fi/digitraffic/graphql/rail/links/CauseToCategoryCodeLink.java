package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.CategoryCode;
import fi.digitraffic.graphql.rail.entities.QCategoryCode;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.CategoryCodeTO;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.CategoryCodeTOConverter;

@Component
public class CauseToCategoryCodeLink extends OneToOneLink<String, CauseTO, CategoryCode, CategoryCodeTO> {
    @Autowired
    private CategoryCodeTOConverter categoryCodeTOConverter;

    @Override
    public String getTypeName() {
        return "Cause";
    }

    @Override
    public String getFieldName() {
        return "categoryCode";
    }

    @Override
    public String createKeyFromParent(CauseTO causeTO) {
        return causeTO.getCategoryCodeOid();
    }

    @Override
    public String createKeyFromChild(CategoryCodeTO categoryCodeTO) {
        return categoryCodeTO.getOid();
    }

    @Override
    public CategoryCodeTO createChildTOFromTuple(Tuple tuple) {
        return categoryCodeTOConverter.convert(tuple);
    }

    @Override
    public Class getEntityClass() {
        return CategoryCode.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.CATEGORY_CODE;
    }

    @Override
    public EntityPath getEntityTable() {
        return QCategoryCode.categoryCode;
    }

    @Override
    public BooleanExpression createWhere(List<String> keys) {
        return QCategoryCode.categoryCode.oid.in(keys);
    }

}
