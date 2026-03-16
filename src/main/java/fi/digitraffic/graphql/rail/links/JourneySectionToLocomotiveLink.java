package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Locomotive;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.model.LocomotiveTO;
import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.LocomotiveTOConverter;

@Component
public class JourneySectionToLocomotiveLink extends OneToManyLink<Long, JourneySectionTO, Locomotive, LocomotiveTO> {

    private final LocomotiveTOConverter locomotiveTOConverter;

    public JourneySectionToLocomotiveLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                          final JpqlOrderByBuilder jpqlOrderByBuilder,
                                          @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                          final LocomotiveTOConverter locomotiveTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.locomotiveTOConverter = locomotiveTOConverter;
    }

    @Override
    public String getTypeName() { return "JourneySection"; }

    @Override
    public String getFieldName() { return "locomotives"; }

    @Override
    public Long createKeyFromParent(final JourneySectionTO journeySectionTO) {
        return (long) journeySectionTO.getId();
    }

    @Override
    public Long createKeyFromChild(final LocomotiveTO locomotiveTO) {
        return (long) locomotiveTO.getJourneysectionId();
    }

    @Override
    public LocomotiveTO createChildTOFromEntity(final Locomotive entity) {
        return locomotiveTOConverter.convertEntity(entity);
    }

    @Override
    public Class<Locomotive> getEntityClass() { return Locomotive.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<Long> keys) {
        return simpleInClause(getEntityAlias() + ".journeysectionId IN :keys", keys);
    }
}

