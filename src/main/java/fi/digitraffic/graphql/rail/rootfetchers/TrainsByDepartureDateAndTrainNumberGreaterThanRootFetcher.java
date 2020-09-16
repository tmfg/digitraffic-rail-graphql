package fi.digitraffic.graphql.rail.rootfetchers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetcher;

@Component
public class TrainsByDepartureDateAndTrainNumberGreaterThanRootFetcher extends BaseRootFetcher<List<TrainTO>> {

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TrainTOConverter trainTOConverter;

    @Value("${digitraffic.max-returned-trains}")
    public Integer MAX_RESULTS;

    @Override
    public String getQueryName() {
        return "trainsByDepartureDateAndTrainNumberGreaterThan";
    }

    public DataFetcher<List<TrainTO>> createFetcher() {
        return dataFetchingEnvironment -> {
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");
            Integer trainNumber = dataFetchingEnvironment.getArgument("trainNumberGreaterThan");

            List<Train> trains = trainRepository.findByDepartureDateWithTrainNumberGreaterThan(departureDate, trainNumber.longValue(), PageRequest.of(0, MAX_RESULTS));
            return trains.stream().map(trainTOConverter::convert).collect(Collectors.toList());
        };
    }
}
