package graphqlscope.graphql.fetchers.base;

import java.util.List;
import java.util.function.Function;

import org.dataloader.BatchLoader;

public abstract class OneToManyDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType> extends BaseDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType, List<ChildTOType>> {
    public BatchLoader<KeyType, List<ChildTOType>> createLoader() {
        Function<ChildEntityType, ChildTOType> childTOConverter = s -> createChildTOToFromChild(s);
        return dataFetcherFactory.createOneToManyDataLoader(
                parentIds -> findChildrenByKeys(parentIds),
                child -> createKeyFromChild(child),
                childTOConverter);
    }
}
