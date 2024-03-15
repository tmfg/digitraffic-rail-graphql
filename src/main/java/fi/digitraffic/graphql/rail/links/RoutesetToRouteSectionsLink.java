package fi.digitraffic.graphql.rail.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.entities.QRoutesection;
import fi.digitraffic.graphql.rail.entities.Routesection;
import fi.digitraffic.graphql.rail.links.base.OneToManyLink;
import fi.digitraffic.graphql.rail.model.RoutesectionTO;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.RoutesectionTOConverter;

@Component
public class RoutesetToRouteSectionsLink extends OneToManyLink<Long, RoutesetMessageTO, Routesection, RoutesectionTO> {
    @Autowired
    private RoutesectionTOConverter routesectionTOConverter;

    @Override
    public String getTypeName() {
        return "RoutesetMessage";
    }

    @Override
    public String getFieldName() {
        return "routesections";
    }

    @Override
    public Long createKeyFromParent(final RoutesetMessageTO routesetMessageTO) {
        return Long.valueOf(routesetMessageTO.getId());
    }

    @Override
    public Long createKeyFromChild(final RoutesectionTO routesectionTO) {
        return Long.valueOf(routesectionTO.getRoutesetId());
    }

    @Override
    public RoutesectionTO createChildTOFromTuple(final Tuple tuple) {
        return routesectionTOConverter.convert(tuple);
    }

    @Override
    public Class<Routesection> getEntityClass() {
        return Routesection.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.ROUTESECTION;
    }

    @Override
    public EntityPath getEntityTable() {
        return QRoutesection.routesection;
    }

    @Override
    public BooleanExpression createWhere(final List<Long> keys) {
        return QRoutesection.routesection.routesetId.in(keys);
    }

    @Override
    public OrderSpecifier createDefaultOrder() {
        return new OrderSpecifier(Order.ASC, QRoutesection.routesection.sectionOrder);
    }
}
