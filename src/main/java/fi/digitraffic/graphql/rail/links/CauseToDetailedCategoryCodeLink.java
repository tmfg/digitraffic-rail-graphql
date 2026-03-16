package fi.digitraffic.graphql.rail.links;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.DetailedCategoryCode;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.model.DetailedCategoryCodeTO;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.DetailedCategoryCodeTOConverter;

@Component
public class CauseToDetailedCategoryCodeLink extends OneToOneLink<String, CauseTO, DetailedCategoryCode, DetailedCategoryCodeTO> {

    private final DetailedCategoryCodeTOConverter detailedCategoryCodeTOConverter;

    public CauseToDetailedCategoryCodeLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                           final JpqlOrderByBuilder jpqlOrderByBuilder,
                                           @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                           final DetailedCategoryCodeTOConverter detailedCategoryCodeTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.detailedCategoryCodeTOConverter = detailedCategoryCodeTOConverter;
    }

    @Override
    public String getTypeName() { return "Cause"; }

    @Override
    public String getFieldName() { return "detailedCategoryCode"; }

    @Override
    public String createKeyFromParent(final CauseTO causeTO) {
        return Objects.requireNonNullElse(causeTO.getDetailedCategoryCodeOid(), "-");
    }

    @Override
    public String createKeyFromChild(final DetailedCategoryCodeTO detailedCategoryCodeTO) {
        return detailedCategoryCodeTO.getOid();
    }

    @Override
    public DetailedCategoryCodeTO createChildTOFromEntity(final DetailedCategoryCode entity) {
        return detailedCategoryCodeTOConverter.convertEntity(entity);
    }

    @Override
    public Class<DetailedCategoryCode> getEntityClass() { return DetailedCategoryCode.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<String> keys) {
        return simpleInClause(getEntityAlias() + ".oid IN :keys", keys);
    }
}

