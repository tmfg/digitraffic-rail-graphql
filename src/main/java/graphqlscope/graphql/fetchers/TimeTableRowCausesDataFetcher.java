package graphqlscope.graphql.fetchers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.entities.TimeTableRowId;
import graphqlscope.graphql.model.CauseTO;
import graphqlscope.graphql.model.TimeTableRowTO;
import graphqlscope.graphql.repositories.CauseRepository;
import graphqlscope.graphql.to.CauseTOConverter;

@Component
public class TimeTableRowCausesDataFetcher extends BaseDataFetcher<TimeTableRowId, List<CauseTO>> {

    @Autowired
    private DataFetcherFactory dataFetcherFactory;

    @Autowired
    private CauseRepository causeRepository;

    @Autowired
    private CauseTOConverter causeTOConverter;

    @Override
    public String getTypeName() {
        return "TimeTableRow";
    }

    @Override
    public String getFieldName() {
        return "causes";
    }

    @Override
    public DataFetcher<CompletableFuture<List<CauseTO>>> createFetcher() {
        return dataFetcherFactory.createDataFetcher("causes", (TimeTableRowTO parent) -> new TimeTableRowId(parent.getId().longValue(), parent.getDepartureDate(), parent.getTrainNumber().longValue()));
    }

    @Override
    public BatchLoader<TimeTableRowId, List<CauseTO>> createLoader() {
        return dataFetcherFactory.createOneToManyDataLoader(
                parentIds -> causeRepository.findAllByTimeTableRowIds(parentIds),
                child -> child.timeTableRowId,
                causeTOConverter::convert);
    }
}
