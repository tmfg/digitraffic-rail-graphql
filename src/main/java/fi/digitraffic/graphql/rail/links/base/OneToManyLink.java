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

                    filterWithSkipAndTake(childrenGroupedBy);

                    return childrenGroupedBy;
                }
        );
    }

    // This should be done in database, but Mysql 5.7 does not support partition...over
    private void filterWithSkipAndTake(Map<KeyType, List<ChildTOType>> childrenGroupedBy) {
        Integer skip = dataFetchingEnvironment.getArgument("skip");
        Integer take = dataFetchingEnvironment.getArgument("take");
        if (skip != null || take != null) {
            Integer start = skip != null ? skip : 0;
            Integer elementsToPotentiallyTake = take != null ? take : Integer.MAX_VALUE;

            for (Map.Entry<KeyType, List<ChildTOType>> keyTypeListEntry : childrenGroupedBy.entrySet()) {
                List<ChildTOType> values = keyTypeListEntry.getValue();
                KeyType key = keyTypeListEntry.getKey();
                if (start < values.size()) {
                    Integer end = Math.min(start + elementsToPotentiallyTake, values.size());
                    childrenGroupedBy.put(key, values.subList(start, end));
                } else {
                    childrenGroupedBy.put(key, new ArrayList<>());
                }
            }
        }
    }

}
