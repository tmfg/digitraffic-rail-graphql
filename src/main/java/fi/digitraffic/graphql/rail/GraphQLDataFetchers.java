package fi.digitraffic.graphql.rail;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import fi.digitraffic.graphql.rail.config.CustomException;
import fi.digitraffic.graphql.rail.entities.Composition;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.entities.TrainLocation;
import fi.digitraffic.graphql.rail.entities.TrainLocationId;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.model.TrainLocationTO;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.repositories.CompositionRepository;
import fi.digitraffic.graphql.rail.repositories.TrainCategoryRepository;
import fi.digitraffic.graphql.rail.repositories.TrainLocationRepository;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import fi.digitraffic.graphql.rail.to.CompositionTOConverter;
import fi.digitraffic.graphql.rail.to.TrainLocationTOConverter;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetcher;

@Component
public class GraphQLDataFetchers {

    public static final int MAX_RESULTS = 250;
    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TrainTOConverter trainTOConverter;

    @Autowired
    private TrainLocationRepository trainLocationRepository;

    @Autowired
    private TrainLocationTOConverter trainLocationTOConverter;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private CompositionTOConverter compositionTOConverter;

    @Autowired
    private TrainCategoryRepository trainCategoryRepository;

    private static final Logger LOG = LoggerFactory.getLogger(GraphQLDataFetchers.class);

    public DataFetcher<Optional<TrainTO>> trainFetcher() {
        return dataFetchingEnvironment -> {
            Integer trainNumber = dataFetchingEnvironment.getArgument("trainNumber");
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            return trainRepository.findById(new TrainId(trainNumber, departureDate)).map(trainTOConverter::convert);
        };
    }

    public DataFetcher<List<TrainTO>> trainsFetcher() {
        return dataFetchingEnvironment -> {
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            List<Train> trains = trainRepository.findByDepartureDate(departureDate, PageRequest.of(0, MAX_RESULTS));
            return trains.stream().map(trainTOConverter::convert).collect(Collectors.toList());
        };
    }

    public DataFetcher<List<TrainTO>> trainsWithVersionGreaterThanFetcher() {
        return dataFetchingEnvironment -> {
            String version = dataFetchingEnvironment.getArgument("version");

            List<Train> trains = trainRepository.findByVersionGreaterThanOrderByVersionAsc(Long.parseLong(version), PageRequest.of(0, MAX_RESULTS));
            return trains.stream().map(trainTOConverter::convert).collect(Collectors.toList());
        };
    }

    public DataFetcher trainsWithTrainNumberGreaterThenFetcher() {
        return dataFetchingEnvironment -> {
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");
            Integer trainNumber = dataFetchingEnvironment.getArgument("trainNumber");

            List<Train> trains = trainRepository.findByDepartureDateWithTrainNumberGreaterThan(departureDate, trainNumber.longValue(), PageRequest.of(0, MAX_RESULTS));
            return trains.stream().map(trainTOConverter::convert).collect(Collectors.toList());
        };
    }

    public DataFetcher trainsByStationAndQuantityFetcher() {
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


    public DataFetcher<List<TrainLocationTO>> trainLocationFetcher() {
        return dataFetchingEnvironment -> {
            Integer trainNumber = dataFetchingEnvironment.getArgument("trainNumber");
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            TrainLocation trainLocation = new TrainLocation();
            trainLocation.trainLocationId = new TrainLocationId(trainNumber.longValue(), departureDate, null);

            List<TrainLocation> trainLocations = trainLocationRepository.findAll(Example.of(trainLocation));
            return trainLocations.stream().map(trainLocationTOConverter::convert).collect(Collectors.toList());
        };
    }

    public DataFetcher<Optional<CompositionTO>> compositionFetcher() {
        return dataFetchingEnvironment -> {
            Integer trainNumber = dataFetchingEnvironment.getArgument("trainNumber");
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            Optional<Composition> composition = compositionRepository.findById(new TrainId(trainNumber.longValue(), departureDate));
            return composition.map(compositionTOConverter::convert);
        };
    }

    public DataFetcher<List<CompositionTO>> compositionsFetcher() {
        return dataFetchingEnvironment -> {
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            List<Composition> compositions = compositionRepository.findByDepartureDate(departureDate);
            return compositions.stream().map(compositionTOConverter::convert).collect(Collectors.toList());
        };
    }

    public DataFetcher<List<CompositionTO>> compositionsGreaterThanVersionFetcher() {
        return dataFetchingEnvironment -> {
            String version = dataFetchingEnvironment.getArgument("version");

            List<Composition> compositions = compositionRepository.findByVersionGreaterThanOrderByVersionAsc(Long.parseLong(version), PageRequest.of(0, MAX_RESULTS));
            return compositions.stream().map(compositionTOConverter::convert).collect(Collectors.toList());
        };
    }
}
