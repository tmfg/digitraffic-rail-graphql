package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dataloader.BatchLoaderWithContext;

import graphql.schema.DataFetchingEnvironment;

public abstract class ManyToManyLink<KeyType, ParentTOType, ChildEntityType, ChildTOType>
        extends BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, List<ChildTOType>> {

    public abstract List<KeyType> createKeysFromChild(final ChildTOType child);

    public KeyType createKeyFromChild(final ChildTOType child) {
        throw new IllegalStateException("Should not be called");
    }

    public BatchLoaderWithContext<KeyType, List<ChildTOType>> createLoader() {
        return createDataLoader((children, dataFetchingEnvironment) -> {
                Map<KeyType, List<ChildTOType>> childrenGroupedBy = new HashMap<>();
                for (ChildTOType child1 : children) {
                    List<KeyType> parentIds = createKeysFromChild(child1);
                    for (final KeyType parentId : parentIds) {
                        if (!childrenGroupedBy.containsKey(parentId)) {
                            childrenGroupedBy.put(parentId, new ArrayList<>());
                        }

                        childrenGroupedBy.get(parentId).add(child1);
                    }
                }

                filterWithSkipAndTake(childrenGroupedBy, dataFetchingEnvironment);

                return childrenGroupedBy;
            }
        );
    }

    // This should be done in database, but Mysql 5.7 does not support partition...over
    private void filterWithSkipAndTake(Map<KeyType, List<ChildTOType>> childrenGroupedBy, DataFetchingEnvironment dataFetchingEnvironment) {
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
