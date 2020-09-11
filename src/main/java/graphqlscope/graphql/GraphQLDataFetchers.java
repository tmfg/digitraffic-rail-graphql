package graphqlscope.graphql;

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

import graphql.schema.DataFetcher;
import graphqlscope.graphql.entities.Composition;
import graphqlscope.graphql.entities.Train;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.entities.TrainLocation;
import graphqlscope.graphql.entities.TrainLocationId;
import graphqlscope.graphql.model.CompositionTO;
import graphqlscope.graphql.model.TrainLocationTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.CompositionRepository;
import graphqlscope.graphql.repositories.TrainLocationRepository;
import graphqlscope.graphql.repositories.TrainRepository;
import graphqlscope.graphql.to.CompositionTOConverter;
import graphqlscope.graphql.to.TrainLocationTOConverter;
import graphqlscope.graphql.to.TrainTOConverter;

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

            List<Train> trains = trainRepository.findByDepartureDate(departureDate);
            return trains.stream().map(trainTOConverter::convert).collect(Collectors.toList());
        };
    }

    public DataFetcher<List<TrainTO>> trainsGreaterThanVersionFetcher() {
        return dataFetchingEnvironment -> {
            String version = dataFetchingEnvironment.getArgument("version");

            List<Train> trains = trainRepository.findByVersionGreaterThanOrderByVersionAsc(Long.parseLong(version), PageRequest.of(0, MAX_RESULTS));
            return trains.stream().map(trainTOConverter::convert).collect(Collectors.toList());
        };
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
