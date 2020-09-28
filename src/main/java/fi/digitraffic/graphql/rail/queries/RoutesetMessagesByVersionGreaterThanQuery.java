package fi.digitraffic.graphql.rail.queries;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.entities.Routeset;
import fi.digitraffic.graphql.rail.model.RoutesetMessageTO;
import fi.digitraffic.graphql.rail.repositories.RoutesetRepository;
import fi.digitraffic.graphql.rail.to.RoutesetMessageTOConverter;
import graphql.schema.DataFetcher;

@Component
public class RoutesetMessagesByVersionGreaterThanQuery extends BaseQuery<List<RoutesetMessageTO>> {

    @Autowired
    private RoutesetRepository routesetRepository;

    @Autowired
    private RoutesetMessageTOConverter routesetMessageTOConverter;

    @Value("${digitraffic.max-returned-rows}")
    public Integer MAX_RESULTS;

    @Override
    public String getQueryName() {
        return "routesetMessagesByVersionGreaterThan";
    }

    public DataFetcher<List<RoutesetMessageTO>> createFetcher() {
        return dataFetchingEnvironment -> {
            String version = dataFetchingEnvironment.getArgument("version");

            List<Routeset> entities = routesetRepository.findByVersionGreaterThanOrderByVersionAsc(Long.parseLong(version), PageRequest.of(0, MAX_RESULTS));
            return entities.stream().map(routesetMessageTOConverter::convert).collect(Collectors.toList());
        };
    }
}
