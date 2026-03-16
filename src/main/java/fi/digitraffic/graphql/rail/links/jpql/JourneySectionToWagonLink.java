package fi.digitraffic.graphql.rail.links.jpql;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Wagon;
import fi.digitraffic.graphql.rail.links.base.jpql.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.jpql.OneToManyLinkJpql;
import fi.digitraffic.graphql.rail.model.JourneySectionTO;
import fi.digitraffic.graphql.rail.model.WagonTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.WagonTOConverter;

@Component
public class JourneySectionToWagonLink extends OneToManyLinkJpql<Long, JourneySectionTO, Wagon, WagonTO> {

    private final WagonTOConverter wagonTOConverter;

    public JourneySectionToWagonLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                     final JpqlOrderByBuilder jpqlOrderByBuilder,
                                     @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                     final WagonTOConverter wagonTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.wagonTOConverter = wagonTOConverter;
    }

    @Override
    public String getTypeName() { return "JourneySection"; }

    @Override
    public String getFieldName() { return "wagons"; }

    @Override
    public Long createKeyFromParent(final JourneySectionTO journeySectionTO) {
        return (long) journeySectionTO.getId();
    }

    @Override
    public Long createKeyFromChild(final WagonTO wagonTO) {
        return (long) wagonTO.getJourneysectionId();
    }

    @Override
    public WagonTO createChildTOFromEntity(final Wagon entity) {
        return wagonTOConverter.convertEntity(entity);
    }

    @Override
    public Class<Wagon> getEntityClass() { return Wagon.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<Long> keys) {
        return simpleInClause(getEntityAlias() + ".journeysectionId IN :keys", keys);
    }
}

