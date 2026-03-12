package fi.digitraffic.graphql.rail.queries.jpql;

import java.time.LocalDate;
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
public class TrainsByDepartureDateQuery extends BaseQueryJpql<Train, TrainTO> {

    private final TrainTOConverter trainTOConverter;

    public TrainsByDepartureDateQuery(final JpqlWhereBuilder whereBuilder,
                                      final JpqlOrderByBuilder orderByBuilder,
                                      @Value("${digitraffic.max-returned-rows}") final int maxResults,
                                      final TrainTOConverter trainTOConverter) {
        super(whereBuilder, orderByBuilder, maxResults);
        this.trainTOConverter = trainTOConverter;
    }

    @Override
    public String getQueryName() {
        return "trainsByDepartureDate";
    }

    @Override
    public Class<Train> getEntityClass() {
        return Train.class;
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                        final Map<String, Object> parameters) {
        final LocalDate departureDate = env.getArgument("departureDate");
        parameters.put("departureDate", departureDate);

        return alias + ".id.departureDate = :departureDate";
    }

    @Override
    public TrainTO convertEntityToTO(final Train entity) {
        return trainTOConverter.convertEntity(entity);
    }
}

