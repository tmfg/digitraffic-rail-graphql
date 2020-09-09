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
import graphqlscope.graphql.model.TimetableTypeTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.TrainRepository;

@Component
public class GraphQLDataFetchers {

    @Autowired
    private TrainRepository trainRepository;

    private static final Logger LOG = LoggerFactory.getLogger(GraphQLDataFetchers.class);

    public DataFetcher trainFetcher() {
        return dataFetchingEnvironment -> {
            Integer trainNumber = dataFetchingEnvironment.getArgument("trainNumber");
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            return trainRepository.findById(new TrainId(trainNumber, departureDate))
                    .map(s -> new TrainTO(
                            s.cancelled,
                            s.commuterLineID,
                            s.deleted,
                            s.id.departureDate,
                            s.operatorShortCode,
                            s.runningCurrently,
                            s.timetableAcceptanceDate,
                            s.timetableType.equals(Train.TimetableType.ADHOC) ? TimetableTypeTO.ADHOC : TimetableTypeTO.REGULAR,
                            s.id.trainNumber.intValue(),
                            s.version.toString(),
                            null,
                            null
                    ));
        };
    }

    public DataFetcher trainsFetcher() {
        return dataFetchingEnvironment -> {
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            List<Train> trains = trainRepository.findByDepartureDate(departureDate);
            return trains.stream()
                    .map(s -> new TrainTO(
                            s.cancelled,
                            s.commuterLineID,
                            s.deleted,
                            s.id.departureDate,
                            s.operatorShortCode,
                            s.runningCurrently,
                            s.timetableAcceptanceDate,
                            s.timetableType.equals(Train.TimetableType.ADHOC) ? TimetableTypeTO.ADHOC : TimetableTypeTO.REGULAR,
                            s.id.trainNumber.intValue(),
                            s.version.toString(),
                            null,
                            null
                    )).collect(Collectors.toList());
        };
    }

    public DataFetcher trainsGreaterThanVersionFetcher() {
        return dataFetchingEnvironment -> {
            String version = dataFetchingEnvironment.getArgument("version");

            List<Train> trains = trainRepository.findByVersionGreaterThanOrderByVersionAsc(Long.parseLong(version), PageRequest.of(0, 2500));
            return trains.stream()
                    .map(s -> new TrainTO(
                            s.cancelled,
                            s.commuterLineID,
                            s.deleted,
                            s.id.departureDate,
                            s.operatorShortCode,
                            s.runningCurrently,
                            s.timetableAcceptanceDate,
                            s.timetableType.equals(Train.TimetableType.ADHOC) ? TimetableTypeTO.ADHOC : TimetableTypeTO.REGULAR,
                            s.id.trainNumber.intValue(),
                            s.version.toString(),
                            null,
                            null
                    )).collect(Collectors.toList());
        };
    }
}
