package graphqlscope.graphql;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.entities.Train;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.repositories.TrainRepository;
import graphqlscope.graphql.to.TrainTOConverter;

@Component
public class GraphQLDataFetchers {

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TrainTOConverter trainTOConverter;

    private static final Logger LOG = LoggerFactory.getLogger(GraphQLDataFetchers.class);

    public DataFetcher trainFetcher() {
        return dataFetchingEnvironment -> {
            Integer trainNumber = dataFetchingEnvironment.getArgument("trainNumber");
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            return trainRepository.findById(new TrainId(trainNumber, departureDate)).map(trainTOConverter::convertTrainToTrainTO);
        };
    }

    public DataFetcher trainsFetcher() {
        return dataFetchingEnvironment -> {
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            List<Train> trains = trainRepository.findByDepartureDate(departureDate);
            return trains.stream().map(trainTOConverter::convertTrainToTrainTO).collect(Collectors.toList());
        };
    }

    public DataFetcher trainsGreaterThanVersionFetcher() {
        return dataFetchingEnvironment -> {
            String version = dataFetchingEnvironment.getArgument("version");

            List<Train> trains = trainRepository.findByVersionGreaterThanOrderByVersionAsc(Long.parseLong(version), PageRequest.of(0, 2500));
            return trains.stream().map(trainTOConverter::convertTrainToTrainTO).collect(Collectors.toList());
        };
    }


}
