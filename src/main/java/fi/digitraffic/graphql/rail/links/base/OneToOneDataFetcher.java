package fi.digitraffic.graphql.rail.links.base;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.dataloader.BatchLoader;

public abstract class OneToOneDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType> extends BaseDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildTOType> {
    public BatchLoader<KeyType, ChildTOType> createLoader() {
        Function<ChildEntityType, ChildTOType> childTOConverter = s -> createChildTOToFromChild(s);
        return createDataLoader(parentIds -> findChildrenByKeys(parentIds), (children) -> {
                    Map<KeyType, ChildTOType> childrenMap = new HashMap<>();
                    for (ChildEntityType child1 : children) {
                        KeyType parentId = ((Function<ChildEntityType, KeyType>) child -> createKeyFromChild(child)).apply(child1);
                        childrenMap.put(parentId, childTOConverter.apply(child1));
                    }

                    return childrenMap;
                }
        );
    }


}
