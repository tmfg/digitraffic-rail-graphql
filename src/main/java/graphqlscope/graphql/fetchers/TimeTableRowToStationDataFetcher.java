package graphqlscope.graphql.fetchers;

import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.model.StationTO;
import graphqlscope.graphql.model.TimeTableRowTO;
import graphqlscope.graphql.repositories.CauseRepository;
import graphqlscope.graphql.repositories.StationRepository;
import graphqlscope.graphql.to.CauseTOConverter;
import graphqlscope.graphql.to.StationTOConverter;

@Component
public class TimeTableRowToStationDataFetcher extends BaseDataFetcher<String, StationTO> {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

    @Autowired
    private CauseRepository causeRepository;

    @Autowired
    private CauseTOConverter causeTOConverter;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationTOConverter stationTOConverter;

    @Override
    public String getTypeName() {
        return "TimeTableRow";
    }

    @Override
    public String getFieldName() {
        return "station";
    }

    @Override
    public DataFetcher<CompletableFuture<StationTO>> createFetcher() {
        return dataFetcherFactory.createDataFetcher("station", (TimeTableRowTO parent) -> parent.getStationShortCode());
    }

    @Override
    public BatchLoader<String, StationTO> createLoader() {
        return dataFetcherFactory.createOneToOneDataLoader(
                parentIds -> stationRepository.findByShortCodeIn(parentIds),
                child -> child.shortCode,
                stationTOConverter::convert);
    }
}
