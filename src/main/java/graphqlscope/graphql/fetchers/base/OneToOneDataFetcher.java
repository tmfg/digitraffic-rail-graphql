package graphqlscope.graphql.fetchers.base;

import java.util.function.Function;

import org.dataloader.BatchLoader;

public abstract class OneToOneDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType> extends BaseDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildTOType> {
    public BatchLoader<KeyType, ChildTOType> createLoader() {
        Function<ChildEntityType, ChildTOType> childTOConverter = s -> createChildTOToFromChild(s);
        return dataFetcherFactory.createOneToOneDataLoader(
                parentIds -> findChildrenByKeys(parentIds),
                child -> createKeyFromChild(child),
                childTOConverter);
    }
}
