package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Routesection;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.RoutesectionTO;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;
import fi.digitraffic.graphql.rail.repositories.RoutesectionRepository;
import fi.digitraffic.graphql.rail.to.RoutesectionTOConverter;

@Component
public class RoutesetToRouteSectionsLink extends OneToManyLink<Long, RoutesetMessageTO, Routesection, RoutesectionTO> {
    @Autowired
    private RoutesectionTOConverter routesectionTOConverter;

    @Autowired
    private RoutesectionRepository routesectionRepository;

    @Override
    public String getTypeName() {
        return "RoutesetMessage";
    }

    @Override
    public String getFieldName() {
        return "routesections";
    }

    @Override
    public Long createKeyFromParent(RoutesetMessageTO routesetMessageTO) {
        return routesetMessageTO.getId().longValue();
    }

    @Override
    public Long createKeyFromChild(Routesection child) {
        return child.routesetId;
    }

    @Override
    public RoutesectionTO createChildTOToFromChild(Routesection child) {
        return routesectionTOConverter.convert(child);
    }

    @Override
    public List<Routesection> findChildrenByKeys(List<Long> keys) {
        return routesectionRepository.findAllByRoutesetIdInOrderBySectionOrderAsc(keys);
    }


}
