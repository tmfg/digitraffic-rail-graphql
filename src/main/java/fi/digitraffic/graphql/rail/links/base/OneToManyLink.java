package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.dataloader.BatchLoaderWithContext;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;

import graphql.schema.DataFetchingEnvironment;

public abstract class OneToManyLink<KeyType, ParentTOType, ChildEntityType, ChildTOType>
        extends BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, List<ChildTOType>> {

    public BatchLoaderWithContext<KeyType, List<ChildTOType>> createLoader() {
        final Function<JPAQueryFactory, JPAQuery<Tuple>> queryAfterFromFunction = (queryFactory) -> queryFactory.select(getFields()).from(getEntityTable());
        return doCreateLoader(queryAfterFromFunction);
    }

    public BatchLoaderWithContext<KeyType, List<ChildTOType>> createLoader(final Function<JPAQueryFactory, JPAQuery<Tuple>> queryAfterFromFunction) {
        return doCreateLoader(queryAfterFromFunction);
    }

    public BatchLoaderWithContext<KeyType, List<ChildTOType>> doCreateLoader(final Function<JPAQueryFactory, JPAQuery<Tuple>> queryAfterFromFunction) {

        return createDataLoader((children, dataFetchingEnvironment) -> {
                    final Map<KeyType, List<ChildTOType>> childrenGroupedBy = new HashMap<>();
                    for (final ChildTOType child1 : children) {
                        final KeyType parentId = createKeyFromChild(child1);
                        final List<ChildTOType> childTOs = childrenGroupedBy.computeIfAbsent(parentId, k -> new ArrayList<>());
                        childTOs.add(child1);
                    }

                    filterWithSkipAndTake(childrenGroupedBy, dataFetchingEnvironment);

                    return childrenGroupedBy;
                }
                , queryAfterFromFunction);
    }

    // This should be done in database, but Mysql 5.7 does not support partition...over
    private void filterWithSkipAndTake(final Map<KeyType, List<ChildTOType>> childrenGroupedBy,
                                       final DataFetchingEnvironment dataFetchingEnvironment) {
        final Integer skip = dataFetchingEnvironment.getArgument("skip");
        final Integer take = dataFetchingEnvironment.getArgument("take");
        if (skip != null || take != null) {
            final int start = skip != null ? skip : 0;
            final int elementsToPotentiallyTake = take != null ? take : Integer.MAX_VALUE;

            for (final Map.Entry<KeyType, List<ChildTOType>> keyTypeListEntry : childrenGroupedBy.entrySet()) {
                final List<ChildTOType> values = keyTypeListEntry.getValue();
                final KeyType key = keyTypeListEntry.getKey();
                if (start < values.size()) {
                    final int end = Math.min(start + elementsToPotentiallyTake, values.size());
                    childrenGroupedBy.put(key, values.subList(start, end));
                } else {
                    childrenGroupedBy.put(key, new ArrayList<>());
                }
            }
        }
    }

}
