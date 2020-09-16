package fi.digitraffic.graphql.rail.queries;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Composition;
import fi.digitraffic.graphql.rail.model.CompositionTO;
import fi.digitraffic.graphql.rail.repositories.CompositionRepository;
import fi.digitraffic.graphql.rail.to.CompositionTOConverter;
import graphql.schema.DataFetcher;

@Component
public class CompositionsGreaterThanVersionRootFetcher extends BaseRootFetcher<List<CompositionTO>> {

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private CompositionTOConverter compositionTOConverter;

    @Value("${digitraffic.max-returned-trains}")
    public Integer MAX_RESULTS;

    @Override
    public String getQueryName() {
        return "compositionsGreaterThanVersion";
    }

    public DataFetcher<List<CompositionTO>> createFetcher() {
        return dataFetchingEnvironment -> {
            String version = dataFetchingEnvironment.getArgument("version");

            List<Composition> compositions = compositionRepository.findByVersionGreaterThanOrderByVersionAsc(Long.parseLong(version), PageRequest.of(0, MAX_RESULTS));
            return compositions.stream().map(compositionTOConverter::convert).collect(Collectors.toList());
        };
    }
}
