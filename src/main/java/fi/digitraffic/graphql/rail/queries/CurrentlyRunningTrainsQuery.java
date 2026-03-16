package fi.digitraffic.graphql.rail.queries;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class CurrentlyRunningTrainsQuery extends BaseQuery<Train, TrainTO> {

    private final TrainTOConverter trainTOConverter;

    public CurrentlyRunningTrainsQuery(final JpqlWhereBuilder whereBuilder,
                                       final JpqlOrderByBuilder orderByBuilder,
                                       @Value("${digitraffic.max-returned-rows}") final int maxResults,
                                       final TrainTOConverter trainTOConverter) {
        super(whereBuilder, orderByBuilder, maxResults);
        this.trainTOConverter = trainTOConverter;
    }

    @Override
    public String getQueryName() {
        return "currentlyRunningTrains";
    }

    @Override
    public Class<Train> getEntityClass() {
        return Train.class;
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                        final Map<String, Object> parameters) {
        final LocalDate today = LocalDate.now(ZoneId.of("Europe/Helsinki"));
        final LocalDate yesterday = today.minusDays(1);

        parameters.put("departureDates", List.of(today, yesterday));

        return alias + ".id.departureDate IN :departureDates AND " + alias + ".runningCurrently = true";
    }

    @Override
    public TrainTO convertEntityToTO(final Train entity) {
        return trainTOConverter.convertEntity(entity);
    }
}

