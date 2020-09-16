package fi.digitraffic.graphql.rail.rootfetchers;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.model.TrainTO;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import fi.digitraffic.graphql.rail.to.TrainTOConverter;
import graphql.schema.DataFetcher;

@Component
public class TrainRootFetcher extends BaseRootFetcher<Optional<TrainTO>> {

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TrainTOConverter trainTOConverter;

    @Override
    public String getQueryName() {
        return "train";
    }

    public DataFetcher<Optional<TrainTO>> createFetcher() {
        return dataFetchingEnvironment -> {
            Integer trainNumber = dataFetchingEnvironment.getArgument("trainNumber");
            LocalDate departureDate = dataFetchingEnvironment.getArgument("departureDate");

            return trainRepository.findById(new TrainId(trainNumber, departureDate)).map(trainTOConverter::convert);
        };
    }
}
