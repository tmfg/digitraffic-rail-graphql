package fi.digitraffic.graphql.rail.queries;

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
public class TrainsByDepartureDateQuery extends BaseQuery<List<TrainTO>> {

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TrainTOConverter trainTOConverter;

    @Value("${digitraffic.max-returned-trains}")
    public Integer MAX_RESULTS;

    @Override
    public String getQueryName() {
        return "trainsByDepartureDate";
    }

    public DataFetcher<List<TrainTO>> createFetcher() {
        return dataFetchingEnvironment -> {
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            List<Train> trains = trainRepository.findByDepartureDate(departureDate, PageRequest.of(0, MAX_RESULTS));
            return trains.stream().map(trainTOConverter::convert).collect(Collectors.toList());
        };
    }
}
