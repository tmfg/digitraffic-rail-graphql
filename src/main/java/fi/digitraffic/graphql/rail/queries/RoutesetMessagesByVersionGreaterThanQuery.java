package fi.digitraffic.graphql.rail.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import fi.digitraffic.graphql.rail.entities.QRouteset;
import fi.digitraffic.graphql.rail.entities.Routeset;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.to.RoutesetMessageTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class RoutesetMessagesByVersionGreaterThanQuery extends BaseQuery<RoutesetMessageTO> {

    @Autowired
    private RoutesetMessageTOConverter routesetMessageTOConverter;

    @Override
    public String getQueryName() {
        return "routesetMessagesByVersionGreaterThan";
    }

    @Override
    public Class getEntityClass() {
        return Routeset.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.ROUTESET;
    }

    @Override
    public EntityPath getEntityTable() {
        return QRouteset.routeset;
    }

    @Override
    public BooleanExpression createWhereFromArguments(DataFetchingEnvironment dataFetchingEnvironment) {
        Long version = Long.parseLong(dataFetchingEnvironment.getArgument("version"));
        return QRouteset.routeset.version.gt(version);
    }

    @Override
    protected JPAQuery<Tuple> createLimitQuery(JPAQuery<Tuple> query, Object limitArgument) {
        return super.createLimitQuery(query, 2000);
    }

    @Override
    public RoutesetMessageTO convertEntityToTO(Tuple tuple) {
        return routesetMessageTOConverter.convert(tuple);
    }

    @Override
    public OrderSpecifier createDefaultOrder() {
        return new OrderSpecifier(Order.ASC, QRouteset.routeset.version);
    }
}
