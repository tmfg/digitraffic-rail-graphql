package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dataloader.BatchLoaderWithContext;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;

import graphql.schema.DataFetchingEnvironment;

public abstract class ManyToManyLink<KeyType, ParentTOType, ChildEntityType, ChildTOType>
        extends BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, List<ChildTOType>> {

    public abstract List<KeyType> createKeysFromChild(final ChildTOType child);

    public KeyType createKeyFromChild(final ChildTOType child) {
        return null;
    }

    public BatchLoaderWithContext<KeyType, List<ChildTOType>> createLoader() {
        final JPAQuery<Tuple> queryAfterFrom = super.queryFactory.select(getFields()).from(getEntityTable());
        return doCreateLoader(queryAfterFrom);
    }

    public BatchLoaderWithContext<KeyType, List<ChildTOType>> createLoader(final JPAQuery<Tuple> queryAfterFrom) {
        return doCreateLoader(queryAfterFrom);
    }

    public BatchLoaderWithContext<KeyType, List<ChildTOType>> doCreateLoader(final JPAQuery<Tuple> queryAfterFrom) {
        return createDataLoader((children, dataFetchingEnvironment) -> {
                    final Map<KeyType, List<ChildTOType>> childrenGroupedByParent = new HashMap<>();
                    for (final ChildTOType child : children) {
                        final List<KeyType> parentIds = createKeysFromChild(child);
                        for (final KeyType parentId : parentIds) {
                            childrenGroupedByParent.computeIfAbsent(parentId, k -> new ArrayList<>()).add(child);
                        }
                    }
                    filterWithSkipAndTake(childrenGroupedByParent, dataFetchingEnvironment);
                    return childrenGroupedByParent;
                }
                , queryAfterFrom);
    }

    // This should be done in database, but Mysql 5.7 does not support partition...over
    private void filterWithSkipAndTake(final Map<KeyType, List<ChildTOType>> childrenGroupedBy,
                                       final DataFetchingEnvironment dataFetchingEnvironment) {
        final Integer skip = dataFetchingEnvironment.getArgument("skip");
        final Integer take = dataFetchingEnvironment.getArgument("take");
        if (skip != null || take != null) {
            final Integer start = skip != null ? skip : 0;
            final Integer elementsToPotentiallyTake = take != null ? take : Integer.MAX_VALUE;

            for (final Map.Entry<KeyType, List<ChildTOType>> keyTypeListEntry : childrenGroupedBy.entrySet()) {
                final List<ChildTOType> values = keyTypeListEntry.getValue();
                final KeyType key = keyTypeListEntry.getKey();
                if (start < values.size()) {
                    final Integer end = Math.min(start + elementsToPotentiallyTake, values.size());
                    childrenGroupedBy.put(key, values.subList(start, end));
                } else {
                    childrenGroupedBy.put(key, new ArrayList<>());
                }
            }
        }
    }
}
