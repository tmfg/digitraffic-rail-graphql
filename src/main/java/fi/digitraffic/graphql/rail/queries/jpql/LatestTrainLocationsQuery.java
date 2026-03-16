package fi.digitraffic.graphql.rail.queries.jpql;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainLocation;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import fi.digitraffic.graphql.rail.repositories.TrainLocationRepository;
import fi.digitraffic.graphql.rail.to.TrainLocationTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class LatestTrainLocationsQuery extends BaseQueryJpql<TrainLocation, TrainLocationTO> {

    private final TrainLocationRepository trainLocationRepository;
    private final TrainLocationTOConverter trainLocationTOConverter;

    public LatestTrainLocationsQuery(final JpqlWhereBuilder whereBuilder,
                                     final JpqlOrderByBuilder orderByBuilder,
                                     @Value("${digitraffic.max-returned-rows}") final int maxResults,
                                     final TrainLocationRepository trainLocationRepository,
                                     final TrainLocationTOConverter trainLocationTOConverter) {
        super(whereBuilder, orderByBuilder, maxResults);
        this.trainLocationRepository = trainLocationRepository;
        this.trainLocationTOConverter = trainLocationTOConverter;
    }

    @Override
    public String getQueryName() {
        return "latestTrainLocations";
    }

    @Override
    public Class<TrainLocation> getEntityClass() {
        return TrainLocation.class;
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                       final Map<String, Object> parameters) {
        final List<Long> ids = trainLocationRepository.findLatest(
                ZonedDateTime.now(ZoneId.of("Europe/Helsinki")).minusMinutes(15));

        if (ids.isEmpty()) {
            return "1 = 0";
        }

        parameters.put("ids", ids);
        return alias + ".id IN :ids";
    }

    @Override
    public TrainLocationTO convertEntityToTO(final TrainLocation entity) {
        return trainLocationTOConverter.convertEntity(entity);
    }
}

