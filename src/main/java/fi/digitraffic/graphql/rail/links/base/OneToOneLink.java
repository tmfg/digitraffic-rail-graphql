package fi.digitraffic.graphql.rail.links.base;

import java.util.HashMap;
import java.util.Map;

import org.dataloader.BatchLoaderWithContext;

public abstract class OneToOneLink<KeyType, ParentTOType, ChildEntityType, ChildTOType>
        extends BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildTOType> {

    public BatchLoaderWithContext<KeyType, ChildTOType> createLoader() {
        return createDataLoader((children, dataFetchingEnvironment) -> {
                Map<KeyType, ChildTOType> childrenMap = new HashMap<>();
                for (ChildTOType child1 : children) {
                    KeyType parentId = createKeyFromChild(child1);
                    childrenMap.put(parentId, child1);
                }

                return childrenMap;
            }
        );
    }

}
