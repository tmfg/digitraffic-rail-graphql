package graphqlscope.graphql.fetchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

@Component
public class DataFetcherFactory {

    public <Parent, ParentId> DataFetcher createDataFetcher(String loaderKey, Function<Parent, ParentId> parentIdProvider) {
        return dataFetchingEnvironment -> {
            Parent parent = dataFetchingEnvironment.getSource();

            DataLoaderRegistry dataLoaderRegistry = dataFetchingEnvironment.getContext();
            DataLoader<ParentId, ?> timeTableRowLoader = dataLoaderRegistry.getDataLoader(loaderKey);

            return timeTableRowLoader.load(parentIdProvider.apply(parent));
        };
    }

    public <ParentId, Child, ChildTO> BatchLoader<ParentId, List<ChildTO>> createDataLoader(
            Function<List<ParentId>, List<Child>> childProvider,
            Function<Child, ParentId> parentIdProvider,
            Function<Child, ChildTO> childTOConverter) {
        return parentIds ->
                CompletableFuture.supplyAsync(() -> {
                            List<Child> children = childProvider.apply(parentIds);

                            Map<ParentId, List<ChildTO>> childrenGroupedBy = new HashMap<>();
                            for (Child child : children) {
                                ParentId parentId = parentIdProvider.apply(child);
                                List<ChildTO> childTOs = childrenGroupedBy.get(parentId);
                                if (childTOs == null) {
                                    childTOs = new ArrayList<>();
                                    childrenGroupedBy.put(parentId, childTOs);
                                }

                                childTOs.add(childTOConverter.apply(child));
                            }

                            return parentIds.stream().map(s -> childrenGroupedBy.get(s)).collect(Collectors.toList());
                        }
                );
    }
}
