package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.dataloader.BatchLoader;

public abstract class OneToManyLink<KeyType, ParentTOType, ChildEntityType, ChildTOType> extends BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, List<ChildTOType>> {
    public BatchLoader<KeyType, List<ChildTOType>> createLoader() {
        return createDataLoader((children) -> {
                    Map<KeyType, List<ChildTOType>> childrenGroupedBy = new HashMap<>();
                    for (ChildTOType child1 : children) {
                        KeyType parentId = ((Function<ChildTOType, KeyType>) child -> createKeyFromChild(child)).apply(child1);
                        List<ChildTOType> childTOs = childrenGroupedBy.get(parentId);
                        if (childTOs == null) {
                            childTOs = new ArrayList<>();
                            childrenGroupedBy.put(parentId, childTOs);
                        }
                        childTOs.add(child1);
                    }

                    return childrenGroupedBy;
                }
        );
    }

}
