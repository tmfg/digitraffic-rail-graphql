package fi.digitraffic.graphql.rail.queries.jpql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Composition;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.CompositionTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class CompositionsGreaterThanVersionQuery extends BaseQueryJpql<Composition, CompositionTO> {

    private static final int MAX_LIMIT = 2000;

    private final CompositionTOConverter compositionTOConverter;

    public CompositionsGreaterThanVersionQuery(final JpqlWhereBuilder whereBuilder,
                                               final JpqlOrderByBuilder orderByBuilder,
                                               @Value("${digitraffic.max-returned-rows}") final int maxResults,
                                               final CompositionTOConverter compositionTOConverter) {
        super(whereBuilder, orderByBuilder, Math.min(maxResults, MAX_LIMIT));
        this.compositionTOConverter = compositionTOConverter;
    }

    @Override
    public String getQueryName() {
        return "compositionsGreaterThanVersion";
    }

    @Override
    public Class<Composition> getEntityClass() {
        return Composition.class;
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
    public CompositionTO convertEntityToTO(final Composition entity) {
        return compositionTOConverter.convertEntity(entity);
    }
}

