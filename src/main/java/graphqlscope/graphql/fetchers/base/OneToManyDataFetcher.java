package graphqlscope.graphql.fetchers.base;

import java.util.List;
import java.util.function.Function;

import org.dataloader.BatchLoader;

public abstract class OneToManyDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType> extends BaseDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType, List<ChildTOType>> {
    public BatchLoader<KeyType, List<ChildTOType>> createLoader() {
        Function<List<KeyType>, List<ChildEntityType>> childrenProvider = parentIds -> findChildrenByKeys(parentIds);
        Function<ChildEntityType, KeyType> childKeyProvider = child -> createKeyFromChild(child);
        Function<ChildEntityType, ChildTOType> childTOConverter = s -> createChildTOToFromChild(s);
        return dataFetcherFactory.createOneToManyDataLoader(childrenProvider, childKeyProvider, childTOConverter);
    }
}
