package fi.digitraffic.graphql.rail.queries;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class TrainQuery extends BaseQuery<Train, TrainTO> {

    private final TrainTOConverter trainTOConverter;

    public TrainQuery(final JpqlWhereBuilder whereBuilder,
                      final JpqlOrderByBuilder orderByBuilder,
                      @Value("${digitraffic.max-returned-rows}") final int maxResults,
                      final TrainTOConverter trainTOConverter) {
        super(whereBuilder, orderByBuilder, maxResults);
        this.trainTOConverter = trainTOConverter;
    }

    @Override
    public String getQueryName() {
        return "train";
    }

    @Override
    public Class<Train> getEntityClass() {
        return Train.class;
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                        final Map<String, Object> parameters) {
        final Integer trainNumber = env.getArgument("trainNumber");
        final LocalDate departureDate = env.getArgument("departureDate");

        parameters.put("trainId", new TrainId(trainNumber, departureDate));

        return alias + ".id = :trainId";
    }

    @Override
    public TrainTO convertEntityToTO(final Train entity) {
        return trainTOConverter.convertEntity(entity);
    }
}

