package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dataloader.BatchLoaderWithContext;
import org.springframework.beans.factory.annotation.Value;

import fi.digitraffic.graphql.rail.query.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.query.JpqlWhereBuilder;
import graphql.schema.DataFetchingEnvironment;

/**
 * One-to-many graph-edge resolver.
 * For relationships where each parent has multiple children (e.g., Train → TimeTableRows).
 */
public abstract class OneToManyLink<KeyType, ParentTOType, ChildEntityType, ChildTOType>
        extends BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, List<ChildTOType>> {

    protected OneToManyLink(final JpqlWhereBuilder jpqlWhereBuilder,
                                final JpqlOrderByBuilder jpqlOrderByBuilder,
                                @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
    }

    @Override
    public BatchLoaderWithContext<KeyType, List<ChildTOType>> createLoader() {
        return createDataLoader((keys, children, dataFetchingEnvironment) -> {
            final Map<KeyType, List<ChildTOType>> childrenGroupedBy = new HashMap<>();

            keys.forEach(key -> childrenGroupedBy.put(key, new ArrayList<>()));

            for (final ChildTOType child : children) {
                final KeyType parentId = createKeyFromChild(child);
                final List<ChildTOType> childTOs = childrenGroupedBy.computeIfAbsent(parentId, k -> new ArrayList<>());
                childTOs.add(child);
            }

            filterWithSkipAndTake(childrenGroupedBy, dataFetchingEnvironment);

            return childrenGroupedBy;
        });
    }

    /**
     * Filters results with skip and take arguments.
     * This should be done in database, but MySQL 5.7 does not support partition...over
     */
    private void filterWithSkipAndTake(final Map<KeyType, List<ChildTOType>> childrenGroupedBy,
                                       final DataFetchingEnvironment dataFetchingEnvironment) {
        final Integer skip = dataFetchingEnvironment.getArgument("skip");
        final Integer take = dataFetchingEnvironment.getArgument("take");
        if (skip != null || take != null) {
            final int start = skip != null ? skip : 0;
            final int elementsToPotentiallyTake = take != null ? take : Integer.MAX_VALUE;

            for (final Map.Entry<KeyType, List<ChildTOType>> entry : childrenGroupedBy.entrySet()) {
                final List<ChildTOType> values = entry.getValue();
                final KeyType key = entry.getKey();
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

