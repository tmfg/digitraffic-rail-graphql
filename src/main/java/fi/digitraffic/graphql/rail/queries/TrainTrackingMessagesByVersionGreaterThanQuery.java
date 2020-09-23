package fi.digitraffic.graphql.rail.queries;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.TrainTrackingMessage;
import fi.digitraffic.graphql.rail.model.TrainTrackingMessageTO;
import fi.digitraffic.graphql.rail.repositories.TrainTrackingMessageRepository;
import fi.digitraffic.graphql.rail.to.TrainTrackingTOConverter;
import graphql.schema.DataFetcher;

@Component
public class TrainTrackingMessagesByVersionGreaterThanQuery extends BaseQuery<List<TrainTrackingMessageTO>> {

    @Autowired
    private TrainTrackingMessageRepository trainTrackingMessageRepository;

    @Autowired
    private TrainTrackingTOConverter trainTrackingTOConverter;

    @Value("${digitraffic.max-returned-train-tracking-messages}")
    public Integer MAX_RESULTS;

    @Override
    public String getQueryName() {
        return "trainTrackingMessagesByVersionGreaterThan";
    }

    public DataFetcher<List<TrainTrackingMessageTO>> createFetcher() {
        return dataFetchingEnvironment -> {
            String version = dataFetchingEnvironment.getArgument("version");

            List<TrainTrackingMessage> entities = trainTrackingMessageRepository.findByVersionGreaterThanOrderByVersionAsc(Long.parseLong(version), PageRequest.of(0, MAX_RESULTS));
            return entities.stream().map(trainTrackingTOConverter::convert).collect(Collectors.toList());
        };
    }
}
