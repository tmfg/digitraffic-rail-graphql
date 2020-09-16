package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.dataloader.BatchLoader;

public abstract class OneToManyLink<KeyType, ParentTOType, ChildEntityType, ChildTOType> extends BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, List<ChildTOType>> {
    public BatchLoader<KeyType, List<ChildTOType>> createLoader() {
        Function<ChildEntityType, ChildTOType> childTOConverter = s -> createChildTOToFromChild(s);
        return createDataLoader(parentIds -> findChildrenByKeys(parentIds), (children) -> {
                    Map<KeyType, List<ChildTOType>> childrenGroupedBy = new HashMap<>();
                    for (ChildEntityType child1 : children) {
                        KeyType parentId = ((Function<ChildEntityType, KeyType>) child -> createKeyFromChild(child)).apply(child1);
                        List<ChildTOType> childTOs = childrenGroupedBy.get(parentId);
                        if (childTOs == null) {
                            childTOs = new ArrayList<>();
                            childrenGroupedBy.put(parentId, childTOs);
                        }
                        childTOs.add(childTOConverter.apply(child1));
                    }

                    return childrenGroupedBy;
                }
        );
    }

}
