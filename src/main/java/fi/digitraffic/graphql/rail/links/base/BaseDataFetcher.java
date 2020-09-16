package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;

import com.google.common.collect.Lists;
import graphql.schema.DataFetcher;

public abstract class BaseDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildFieldType> {
    public abstract String getTypeName();

    public abstract String getFieldName();

    public abstract KeyType createKeyFromParent(ParentTOType parent);

    public abstract KeyType createKeyFromChild(ChildEntityType child);

    public abstract ChildTOType createChildTOToFromChild(ChildEntityType child);

    public abstract List<ChildEntityType> findChildrenByKeys(List<KeyType> keys);

    public abstract BatchLoader<KeyType, ChildFieldType> createLoader();

    public DataFetcher<CompletableFuture<ChildFieldType>> createFetcher() {
        return dataFetchingEnvironment -> {
            ParentTOType parent = dataFetchingEnvironment.getSource();

            DataLoaderRegistry dataLoaderRegistry = dataFetchingEnvironment.getContext();
            DataLoader<KeyType, ChildFieldType> timeTableRowLoader = dataLoaderRegistry.getDataLoader(getFieldName());

            return timeTableRowLoader.load(createKeyFromParent(parent));
        };
    }

    protected <ParentId, Child, ResultType> BatchLoader<ParentId, ResultType> createDataLoader(Function<List<ParentId>, List<Child>> childProvider, Function<List<Child>, Map<ParentId, ResultType>> childGroupFunction) {
        return parentIds -> CompletableFuture.supplyAsync(() -> {
                    List<List<ParentId>> partitions = Lists.partition(parentIds, 2499);
                    List<Child> children = new ArrayList<>(parentIds.size());
                    for (List<ParentId> partition : partitions) {
                        children.addAll(childProvider.apply(partition));
                    }

                    Map<ParentId, ResultType> childrenGroupedBy = childGroupFunction.apply(children);

                    return parentIds.stream().map(s -> childrenGroupedBy.get(s)).collect(Collectors.toList());
                }
        );
    }
}
