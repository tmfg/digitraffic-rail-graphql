package fi.digitraffic.graphql.rail.links;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.ThirdCategoryCode;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToOneLink;
import fi.digitraffic.graphql.rail.model.CauseTO;
import fi.digitraffic.graphql.rail.model.ThirdCategoryCodeTO;
import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.ThirdCategoryCodeTOConverter;

@Component
public class CauseToThirdCategoryCodeLink extends OneToOneLink<String, CauseTO, ThirdCategoryCode, ThirdCategoryCodeTO> {

    private final ThirdCategoryCodeTOConverter thirdCategoryCodeTOConverter;

    public CauseToThirdCategoryCodeLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                        final JpqlOrderByBuilder jpqlOrderByBuilder,
                                        @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                        final ThirdCategoryCodeTOConverter thirdCategoryCodeTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.thirdCategoryCodeTOConverter = thirdCategoryCodeTOConverter;
    }

    @Override
    public String getTypeName() { return "Cause"; }

    @Override
    public String getFieldName() { return "thirdCategoryCode"; }

    @Override
    public String createKeyFromParent(final CauseTO causeTO) {
        return Objects.requireNonNullElse(causeTO.getThirdCategoryCodeOid(), "-");
    }

    @Override
    public String createKeyFromChild(final ThirdCategoryCodeTO thirdCategoryCodeTO) {
        return thirdCategoryCodeTO.getOid();
    }

    @Override
    public ThirdCategoryCodeTO createChildTOFromEntity(final ThirdCategoryCode entity) {
        return thirdCategoryCodeTOConverter.convertEntity(entity);
    }

    @Override
    public Class<ThirdCategoryCode> getEntityClass() { return ThirdCategoryCode.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<String> keys) {
        return simpleInClause(getEntityAlias() + ".oid IN :keys", keys);
    }
}

