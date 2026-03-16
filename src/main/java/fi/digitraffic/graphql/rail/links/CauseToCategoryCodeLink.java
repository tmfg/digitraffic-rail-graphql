package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.CategoryCode;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.CategoryCodeTO;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.CategoryCodeTOConverter;

@Component
public class CauseToCategoryCodeLink extends OneToOneLink<String, CauseTO, CategoryCode, CategoryCodeTO> {

    private final CategoryCodeTOConverter categoryCodeTOConverter;

    public CauseToCategoryCodeLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                   final JpqlOrderByBuilder jpqlOrderByBuilder,
                                   @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                   final CategoryCodeTOConverter categoryCodeTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.categoryCodeTOConverter = categoryCodeTOConverter;
    }

    @Override
    public String getTypeName() { return "Cause"; }

    @Override
    public String getFieldName() { return "categoryCode"; }

    @Override
    public String createKeyFromParent(final CauseTO causeTO) {
        return causeTO.getCategoryCodeOid();
    }

    @Override
    public String createKeyFromChild(final CategoryCodeTO categoryCodeTO) {
        return categoryCodeTO.getOid();
    }

    @Override
    public CategoryCodeTO createChildTOFromEntity(final CategoryCode entity) {
        return categoryCodeTOConverter.convertEntity(entity);
    }

    @Override
    public Class<CategoryCode> getEntityClass() { return CategoryCode.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<String> keys) {
        return simpleInClause(getEntityAlias() + ".oid IN :keys", keys);
    }
}

