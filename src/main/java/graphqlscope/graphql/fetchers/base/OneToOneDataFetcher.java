package graphqlscope.graphql.fetchers.base;

import java.util.List;
import java.util.function.Function;

import org.dataloader.BatchLoader;

public abstract class OneToOneDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType> extends BaseDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildTOType> {
    public BatchLoader<KeyType, ChildTOType> createLoader() {
        Function<List<KeyType>, List<ChildEntityType>> childrenProvider = parentIds -> findChildrenByKeys(parentIds);
        Function<ChildEntityType, KeyType> childKeyProvider = child -> createKeyFromChild(child);
        Function<ChildEntityType, ChildTOType> childTOConverter = s -> createChildTOToFromChild(s);
        return dataFetcherFactory.createOneToOneDataLoader(childrenProvider, childKeyProvider, childTOConverter);
    }
}
