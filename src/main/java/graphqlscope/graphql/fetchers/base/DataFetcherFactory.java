package graphqlscope.graphql.fetchers.base;

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

import com.google.common.collect.Lists;
import graphql.schema.DataFetcher;

@Component
public class DataFetcherFactory {

    public <Parent, ParentId, Child> DataFetcher<CompletableFuture<Child>> createDataFetcher(String loaderKey, Function<Parent, ParentId> parentIdProvider) {
        return dataFetchingEnvironment -> {
            Parent parent = dataFetchingEnvironment.getSource();

            DataLoaderRegistry dataLoaderRegistry = dataFetchingEnvironment.getContext();
            DataLoader<ParentId, Child> timeTableRowLoader = dataLoaderRegistry.getDataLoader(loaderKey);

            return timeTableRowLoader.load(parentIdProvider.apply(parent));
        };
    }

    public <ParentId, Child, ChildTO> BatchLoader<ParentId, List<ChildTO>> createOneToManyDataLoader(
            Function<List<ParentId>, List<Child>> childProvider,
            Function<Child, ParentId> parentIdProvider,
            Function<Child, ChildTO> childTOConverter) {
        return parentIds -> CompletableFuture.supplyAsync(() -> {
            List<List<ParentId>> partitions = Lists.partition(parentIds, 999);
            List<Child> children = new ArrayList<>(parentIds.size());
            for (List<ParentId> partition : partitions) {
                children.addAll(childProvider.apply(partition));
            }

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

    public <ParentId, Child, ChildTO> BatchLoader<ParentId, ChildTO> createOneToOneDataLoader(
            Function<List<ParentId>, List<Child>> childProvider,
            Function<Child, ParentId> parentIdProvider,
            Function<Child, ChildTO> childTOConverter) {
        return parentIds -> CompletableFuture.supplyAsync(() -> {
                    List<List<ParentId>> partitions = Lists.partition(parentIds, 999);
                    List<Child> children = new ArrayList<>(parentIds.size());
                    for (List<ParentId> partition : partitions) {
                        children.addAll(childProvider.apply(partition));
                    }

                    Map<ParentId, ChildTO> childrenMap = new HashMap<>();
                    for (Child child : children) {
                        ParentId parentId = parentIdProvider.apply(child);
                        childrenMap.put(parentId, childTOConverter.apply(child));
                    }

                    return parentIds.stream().map(s -> childrenMap.get(s)).collect(Collectors.toList());
                }
        );
    }
}
