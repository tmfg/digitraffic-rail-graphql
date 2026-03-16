package fi.digitraffic.graphql.rail.queries.jpql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.RoutesetMessage;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.RoutesetMessageTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class RoutesetMessagesByVersionGreaterThanQuery extends BaseQueryJpql<RoutesetMessage, RoutesetMessageTO> {

    private static final int MAX_LIMIT = 2000;

    private final RoutesetMessageTOConverter routesetMessageTOConverter;

    public RoutesetMessagesByVersionGreaterThanQuery(final JpqlWhereBuilder whereBuilder,
                                                     final JpqlOrderByBuilder orderByBuilder,
                                                     @Value("${digitraffic.max-returned-rows}") final int maxResults,
                                                     final RoutesetMessageTOConverter routesetMessageTOConverter) {
        super(whereBuilder, orderByBuilder, Math.min(maxResults, MAX_LIMIT));
        this.routesetMessageTOConverter = routesetMessageTOConverter;
    }

    @Override
    public String getQueryName() {
        return "routesetMessagesByVersionGreaterThan";
    }

    @Override
    public Class<RoutesetMessage> getEntityClass() {
        return RoutesetMessage.class;
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                       final Map<String, Object> parameters) {
        final Long version = Long.parseLong(env.getArgument("version"));
        parameters.put("version", version);
        return alias + ".version > :version";
    }

    @Override
    public String getDefaultOrderBy(final String alias) {
        return alias + ".version ASC";
    }

    @Override
    public RoutesetMessageTO convertEntityToTO(final RoutesetMessage entity) {
        return routesetMessageTOConverter.convertEntity(entity);
    }
}

