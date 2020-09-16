package fi.digitraffic.graphql.rail.rootfetchers;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import fi.digitraffic.graphql.rail.config.CustomException;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.repositories.TrainCategoryRepository;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetcher;

@Component
public class TrainByStationAndQuantityRootFetcher extends BaseRootFetcher<List<TrainTO>> {
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

    public DataFetcher<List<TrainTO>> createFetcher() {
        return dataFetchingEnvironment -> {
            String station = dataFetchingEnvironment.getArgument("station");
            Integer arrivedTrains = dataFetchingEnvironment.getArgument("arrivedTrains");
            Integer arrivingTrains = dataFetchingEnvironment.getArgument("arrivingTrains");
            Integer departedTrains = dataFetchingEnvironment.getArgument("departedTrains");
            Integer departingTrains = dataFetchingEnvironment.getArgument("departingTrains");
            Boolean includeNonStopping = dataFetchingEnvironment.getArgument("includeNonStopping");
            List<String> trainCategoryNames = dataFetchingEnvironment.getArgument("trainCategories");

            if (arrivedTrains == null) {
                arrivedTrains = 5;
            }
            if (arrivingTrains == null) {
                arrivingTrains = 5;
            }
            if (departedTrains == null) {
                departedTrains = 5;
            }
            if (departingTrains == null) {
                departingTrains = 5;
            }
            if (includeNonStopping == null) {
                includeNonStopping = false;
            }

            if (arrivedTrains + arrivingTrains + departedTrains + departingTrains > 250) {
                throw new CustomException(400, "Can not return more than 250 trains");
            }

            List<Long> trainCategoryIds;
            if (trainCategoryNames == null) {
                trainCategoryIds = trainCategoryRepository.findAll().stream().map(s -> s.id).collect(Collectors.toList());
            } else {
                trainCategoryIds = trainCategoryRepository.findAllByNameIn(trainCategoryNames);
            }

            List<Train> trains = getLiveTrainsUsingQuantityFiltering(station, -1L, arrivedTrains, arrivingTrains, departedTrains, departingTrains, includeNonStopping, trainCategoryIds);
            return trains.stream().map(trainTOConverter::convert).collect(Collectors.toList());
        };
    }

    private List<Train> getLiveTrainsUsingQuantityFiltering(String station, long version, int arrived_trains, int arriving_trains,
                                                            int departedTrains, int departingTrains, Boolean includeNonstopping, List<Long> trainCategoryIds) {
        List<Object[]> liveTrains = trainRepository.findLiveTrainsIds(station, departedTrains, departingTrains, arrived_trains,
                arriving_trains, !includeNonstopping, trainCategoryIds);

        List<TrainId> trainsToRetrieve = extractNewerTrainIds(version, liveTrains);

        if (!trainsToRetrieve.isEmpty()) {
            return trainRepository.findAllById(trainsToRetrieve);
        } else {
            return Lists.newArrayList();
        }
    }

    private List<TrainId> extractNewerTrainIds(long version, List<Object[]> liveTrains) {
        return liveTrains.stream().filter(train -> ((BigInteger) train[3]).longValue() > version).map(tuple -> {
            LocalDate departureDate = LocalDate.from(((Date) tuple[1]).toLocalDate());
            BigInteger trainNumber = (BigInteger) tuple[2];
            return new TrainId(trainNumber.longValue(), departureDate);
        }).collect(Collectors.toList());
    }
}
