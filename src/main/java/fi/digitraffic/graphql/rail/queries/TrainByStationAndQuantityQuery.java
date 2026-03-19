package fi.digitraffic.graphql.rail.queries;

import static com.google.common.base.MoreObjects.firstNonNull;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.config.graphql.CustomException;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.links.base.TrainIdWhereClause;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.repositories.TrainCategoryRepository;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;

import graphql.schema.DataFetchingEnvironment;

@Component
public class TrainByStationAndQuantityQuery extends BaseQuery<Train, TrainTO> {

    private final TrainRepository trainRepository;
    private final TrainTOConverter trainTOConverter;
    private final TrainCategoryRepository trainCategoryRepository;

    public TrainByStationAndQuantityQuery(final JpqlWhereBuilder whereBuilder,
                                          final JpqlOrderByBuilder orderByBuilder,
                                          @Value("${digitraffic.max-returned-rows}") final int maxResults,
                                          final TrainRepository trainRepository,
                                          final TrainTOConverter trainTOConverter,
                                          final TrainCategoryRepository trainCategoryRepository) {
        super(whereBuilder, orderByBuilder, maxResults);
        this.trainRepository = trainRepository;
        this.trainTOConverter = trainTOConverter;
        this.trainCategoryRepository = trainCategoryRepository;
    }

    @Override
    public String getQueryName() {
        return "trainsByStationAndQuantity";
    }

    @Override
    public Class<Train> getEntityClass() {
        return Train.class;
    }

    @Override
    public String buildBaseWhereClause(final String alias, final DataFetchingEnvironment env,
                                       final Map<String, Object> parameters) {
        final String station = env.getArgument("station");
        final int arrivedTrains = firstNonNull(env.getArgument("arrivedTrains"), 5);
        final int arrivingTrains = firstNonNull(env.getArgument("arrivingTrains"), 5);
        final int departedTrains = firstNonNull(env.getArgument("departedTrains"), 5);
        final int departingTrains = firstNonNull(env.getArgument("departingTrains"), 5);
        final Boolean includeNonStopping = firstNonNull(env.getArgument("includeNonStopping"), false);
        final List<String> trainCategoryNames = env.getArgument("trainCategories");

        if (arrivedTrains + arrivingTrains + departedTrains + departingTrains > maxResults) {
            throw new CustomException(400, "Can not return more than " + maxResults + " rows");
        }

        final List<Long> trainCategoryIds;
        if (trainCategoryNames == null) {
            trainCategoryIds = trainCategoryRepository.findAll().stream()
                    .map(s -> s.id)
                    .collect(Collectors.toList());
        } else {
            trainCategoryIds = trainCategoryRepository.findAllByNameIn(trainCategoryNames);
        }

        final List<Object[]> liveTrains = trainRepository.findLiveTrainsIds(
                station, departedTrains, departingTrains, arrivedTrains, arrivingTrains,
                !includeNonStopping, trainCategoryIds);

        final List<TrainId> trainIds = liveTrains.stream()
                .map(row -> {
                    final LocalDate departureDate;
                    if (row[1] instanceof LocalDate ld) {
                        departureDate = ld;
                    } else {
                        departureDate = ((Date) row[1]).toLocalDate();
                    }
                    final Long trainNumber = (Long) row[2];
                    return new TrainId(trainNumber, departureDate);
                })
                .collect(Collectors.toList());

        if (trainIds.isEmpty()) {
            // No matching trains — produce a condition that is always false
            return "1 = 0";
        }

        final var keyWhere = TrainIdWhereClause.build(alias, "id.departureDate", "id.trainNumber", trainIds);
        parameters.putAll(keyWhere.params());
        return keyWhere.jpql();
    }

    @Override
    public TrainTO convertEntityToTO(final Train entity) {
        return trainTOConverter.convertEntity(entity);
    }
}

