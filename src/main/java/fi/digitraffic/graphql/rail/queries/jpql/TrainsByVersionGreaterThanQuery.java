package fi.digitraffic.graphql.rail.queries.jpql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class TrainsByVersionGreaterThanQuery extends BaseQueryJpql<Train, TrainTO> {

    private static final int MAX_LIMIT = 2000;

    private final TrainTOConverter trainTOConverter;

    public TrainsByVersionGreaterThanQuery(final JpqlWhereBuilder whereBuilder,
                                           final JpqlOrderByBuilder orderByBuilder,
                                           @Value("${digitraffic.max-returned-rows}") final int maxResults,
                                           final TrainTOConverter trainTOConverter) {
        super(whereBuilder, orderByBuilder, Math.min(maxResults, MAX_LIMIT));
        this.trainTOConverter = trainTOConverter;
    }

    @Override
    public String getQueryName() {
        return "trainsByVersionGreaterThan";
    }

    @Override
    public Class<Train> getEntityClass() {
        return Train.class;
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
    public TrainTO convertEntityToTO(final Train entity) {
        return trainTOConverter.convertEntity(entity);
    }
}

