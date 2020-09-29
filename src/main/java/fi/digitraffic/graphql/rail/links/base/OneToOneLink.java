package fi.digitraffic.graphql.rail.links.base;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.dataloader.BatchLoader;

public abstract class OneToOneLink<KeyType, ParentTOType, ChildEntityType, ChildTOType> extends BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildTOType> {
    public BatchLoader<KeyType, ChildTOType> createLoader() {
        return createDataLoader((children) -> {
                    Map<KeyType, ChildTOType> childrenMap = new HashMap<>();
                    for (ChildTOType child1 : children) {
                        KeyType parentId = ((Function<ChildTOType, KeyType>) child -> createKeyFromChild(child)).apply(child1);
                        childrenMap.put(parentId, child1);
                    }

                    return childrenMap;
                }
        );
    }


}
