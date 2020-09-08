package graphqlscope.graphql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphqlscope.graphql.entities.Cause;
import graphqlscope.graphql.entities.TimeTableRow;
import graphqlscope.graphql.entities.TimeTableRowId;
import graphqlscope.graphql.entities.Train;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.model.CauseTO;
import graphqlscope.graphql.model.TimeTableRowTO;
import graphqlscope.graphql.model.TimetableTypeTO;
import graphqlscope.graphql.model.TrainTO;
import graphqlscope.graphql.repositories.CauseRepository;
import graphqlscope.graphql.repositories.TimeTableRowRepository;
import graphqlscope.graphql.repositories.TrainRepository;

@Component
public class GraphQLDataFetchers {

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TimeTableRowRepository timeTableRowRepository;

    @Autowired
    private CauseRepository causeRepository;

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
                            s.id.departureDate.toString(),
                            s.runningCurrently,
                            s.timetableAcceptanceDate.toString(),
                            s.timetableType.equals(Train.TimetableType.ADHOC) ? TimetableTypeTO.ADHOC : TimetableTypeTO.REGULAR,
                            s.id.trainNumber.intValue(),
                            s.version.toString(),
                            null
                    ));
        };
    }

    public DataFetcher trainTimeTableRowsFetcher() {
        return dataFetchingEnvironment -> {
            TrainTO parent = dataFetchingEnvironment.getSource();


            DataLoaderRegistry dataLoaderRegistry = dataFetchingEnvironment.getContext();
            DataLoader<TrainId, TimeTableRowTO> timeTableRowLoader = dataLoaderRegistry.getDataLoader("timeTableRows");

            return timeTableRowLoader.load(new TrainId(parent.getTrainNumber().longValue(), LocalDate.parse(parent.getDepartureDate())));
        };
    }

    public BatchLoader<TrainId, List<TimeTableRowTO>> timeTableRowBatchLoader() {
        return trainIds ->
                CompletableFuture.supplyAsync(() -> {
                            List<TimeTableRow> timeTableRows = timeTableRowRepository.findAllByTrainIds(trainIds);

                            Map<TrainId, List<TimeTableRowTO>> timeTableRowsGroupBy = new HashMap<>();
                            for (TimeTableRow timeTableRow : timeTableRows) {
                                TrainId trainId = new TrainId(timeTableRow.id.trainNumber, timeTableRow.id.departureDate);
                                List<TimeTableRowTO> timeTableRowTOS = timeTableRowsGroupBy.get(trainId);
                                if (timeTableRowTOS == null) {
                                    timeTableRowTOS = new ArrayList<>();
                                    timeTableRowsGroupBy.put(trainId, timeTableRowTOS);
                                }

                                timeTableRowTOS.add(new TimeTableRowTO("a", 1, "b", "c", true, true, "d", true, timeTableRow.scheduledTime, null, timeTableRow.id.attapId.intValue(), timeTableRow.id
                                        .trainNumber.intValue(), timeTableRow.id.departureDate.toString()));
                            }

                            return trainIds.stream().map(s -> timeTableRowsGroupBy.get(s)).collect(Collectors.toList());
                        }
                );
    }

    public DataFetcher timeTableRowCauseFetcher() {
        return dataFetchingEnvironment -> {
            TimeTableRowTO parent = dataFetchingEnvironment.getSource();

            DataLoaderRegistry dataLoaderRegistry = dataFetchingEnvironment.getContext();
            DataLoader<TimeTableRowId, CauseTO> timeTableRowLoader = dataLoaderRegistry.getDataLoader("causes");

            return timeTableRowLoader.load(new TimeTableRowId(parent.getId().longValue(), LocalDate.parse(parent.getDepartureDate()), parent.getTrainNumber().longValue()));
        };
    }

    public BatchLoader<TimeTableRowId, List<CauseTO>> causeBatchLoader() {
        return parentIds ->
                CompletableFuture.supplyAsync(() -> {
                            List<Cause> children = causeRepository.findAllByTimeTableRowIds(parentIds);

                            Map<TimeTableRowId, List<CauseTO>> childrenGroupedBy = new HashMap<>();
                            for (Cause child : children) {
                                TimeTableRowId parentId = child.timeTableRowId;
                                List<CauseTO> childTOs = childrenGroupedBy.get(parentId);
                                if (childTOs == null) {
                                    childTOs = new ArrayList<>();
                                    childrenGroupedBy.put(parentId, childTOs);
                                }

                                childTOs.add(new CauseTO(child.timeTableRowId.attapId.intValue(), child.timeTableRowId.trainNumber.intValue(), child.timeTableRowId.departureDate.toString()));
                            }

                            return parentIds.stream().map(s -> childrenGroupedBy.get(s)).collect(Collectors.toList());
                        }
                );
    }
}
