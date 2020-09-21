package fi.digitraffic.graphql.rail.queries;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.model.TrainLocationTO;
import fi.digitraffic.graphql.rail.repositories.TrainLocationRepository;
import fi.digitraffic.graphql.rail.to.TrainLocationTOConverter;
import graphql.schema.DataFetcher;

@Component
public class LatestTrainLocationsQuery extends BaseQuery<List<TrainLocationTO>> {

    @Autowired
    private TrainLocationRepository trainLocationRepository;

    @Autowired
    private TrainLocationTOConverter trainLocationTOConverter;

    @Override
    public String getQueryName() {
        return "latestTrainLocations";
    }

    public DataFetcher<List<TrainLocationTO>> createFetcher() {
        return dataFetchingEnvironment -> {
            List<Long> ids = trainLocationRepository.findLatest(ZonedDateTime.now(ZoneId.of("Europe/Helsinki")).minusMinutes(15));
            return trainLocationRepository.findAllById(ids).stream().map(s -> trainLocationTOConverter.convert(s)).collect(Collectors.toList());
        };
    }
}
