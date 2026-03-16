package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Routesection;
import fi.digitraffic.graphql.rail.links.base.KeyWhereClause;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.RoutesectionTO;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.RoutesectionTOConverter;

@Component
public class RoutesetToRouteSectionsLink extends OneToManyLink<Long, RoutesetMessageTO, Routesection, RoutesectionTO> {

    private final RoutesectionTOConverter routesectionTOConverter;

    public RoutesetToRouteSectionsLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                       final JpqlOrderByBuilder jpqlOrderByBuilder,
                                       @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize,
                                       final RoutesectionTOConverter routesectionTOConverter) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
        this.routesectionTOConverter = routesectionTOConverter;
    }

    @Override
    public String getTypeName() { return "RoutesetMessage"; }

    @Override
    public String getFieldName() { return "routesections"; }

    @Override
    public Long createKeyFromParent(final RoutesetMessageTO routesetMessageTO) {
        return (long) routesetMessageTO.getId();
    }

    @Override
    public Long createKeyFromChild(final RoutesectionTO routesectionTO) {
        return (long) routesectionTO.getRoutesetId();
    }

    @Override
    public RoutesectionTO createChildTOFromEntity(final Routesection entity) {
        return routesectionTOConverter.convertEntity(entity);
    }

    @Override
    public Class<Routesection> getEntityClass() { return Routesection.class; }

    @Override
    protected KeyWhereClause buildKeyWhereClause(final List<Long> keys) {
        return simpleInClause(getEntityAlias() + ".routesetId IN :keys", keys);
    }

    @Override
    public String getDefaultOrderBy() {
        return getEntityAlias() + ".sectionOrder ASC";
    }
}

