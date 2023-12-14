package fi.digitraffic.graphql.rail.queries;

import static com.google.common.base.MoreObjects.firstNonNull;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import fi.digitraffic.graphql.rail.config.graphql.CustomException;
import fi.digitraffic.graphql.rail.entities.QTrain;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.querydsl.AllFields;
import fi.digitraffic.graphql.rail.repositories.TrainCategoryRepository;
import fi.digitraffic.graphql.rail.repositories.TrainIdOptimizer;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetchingEnvironment;

@Component
public class TrainByStationAndQuantityQuery extends BaseQuery<TrainTO> {
    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TrainTOConverter trainTOConverter;

    @Autowired
    private TrainCategoryRepository trainCategoryRepository;

    @Override
    public String getQueryName() {
        return "trainsByStationAndQuantity";
    }

    @Override
    public Class getEntityClass() {
        return Train.class;
    }

    @Override
    public Expression[] getFields() {
        return AllFields.TRAIN;
    }

    @Override
    public EntityPath getEntityTable() {
        return QTrain.train;
    }

    @Override
    public BooleanExpression createWhereFromArguments(final DataFetchingEnvironment dataFetchingEnvironment) {
        final String station = dataFetchingEnvironment.getArgument("station");
        final int arrivedTrains = firstNonNull(dataFetchingEnvironment.getArgument("arrivedTrains"), 5);
        final int arrivingTrains = firstNonNull(dataFetchingEnvironment.getArgument("arrivingTrains"), 5);
        final int departedTrains = firstNonNull(dataFetchingEnvironment.getArgument("departedTrains"), 5);
        final int departingTrains = firstNonNull(dataFetchingEnvironment.getArgument("departingTrains"), 5);
        final Boolean includeNonStopping = firstNonNull(dataFetchingEnvironment.getArgument("includeNonStopping"), false);
        final List<String> trainCategoryNames = dataFetchingEnvironment.getArgument("trainCategories");

        if (arrivedTrains + arrivingTrains + departedTrains + departingTrains > MAX_RESULTS) {
            throw new CustomException(400, "Can not return more than " + MAX_RESULTS + " rows");
        }

        final List<Long> trainCategoryIds;
        if (trainCategoryNames == null) {
            trainCategoryIds = trainCategoryRepository.findAll().stream().map(s -> s.id).collect(Collectors.toList());
        } else {
            trainCategoryIds = trainCategoryRepository.findAllByNameIn(trainCategoryNames);
        }

        final List<TrainId> trainIds = getLiveTrainsUsingQuantityFiltering(station, -1L,
                arrivedTrains,
                arrivingTrains,
                departedTrains,
                departingTrains,
                includeNonStopping, trainCategoryIds);
        if (trainIds.isEmpty()) {
            trainIds.add(new TrainId(-9999L, LocalDate.now()));
        }

        return TrainIdOptimizer.optimize(QTrain.train.id, trainIds);
    }

    @Override
    public TrainTO convertEntityToTO(final Tuple tuple) {
        return trainTOConverter.convert(tuple);
    }

    private List<TrainId> getLiveTrainsUsingQuantityFiltering(final String station,
                                                              final long version,
                                                              final int arrived_trains,
                                                              final int arriving_trains,
                                                              final int departedTrains,
                                                              final int departingTrains,
                                                              final Boolean includeNonstopping,
                                                              final List<Long> trainCategoryIds) {
        final List<Object[]> liveTrains = trainRepository.findLiveTrainsIds(station, departedTrains, departingTrains, arrived_trains,
                arriving_trains, !includeNonstopping, trainCategoryIds);

        return extractNewerTrainIds(version, liveTrains);


    }

    private List<TrainId> extractNewerTrainIds(final long version, final List<Object[]> liveTrains) {
        return liveTrains.stream().filter(train -> ((Long) train[3]) > version).map(tuple -> {
            final LocalDate departureDate = LocalDate.from(((Date) tuple[1]).toLocalDate());
            final Long trainNumber = (Long) tuple[2];
            return new TrainId(trainNumber, departureDate);
        }).collect(Collectors.toList());
    }
}
