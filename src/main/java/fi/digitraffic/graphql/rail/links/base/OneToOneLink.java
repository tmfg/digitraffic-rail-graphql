package fi.digitraffic.graphql.rail.links.base;

import java.util.HashMap;
import java.util.Map;

import org.dataloader.BatchLoaderWithContext;
import org.springframework.beans.factory.annotation.Value;

import fi.digitraffic.graphql.rail.queries.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;

/**
 * One-to-one graph-edge resolver.
 * For relationships where each parent has exactly one child (e.g., TimeTableRow → Station).
 */
public abstract class OneToOneLink<KeyType, ParentTOType, ChildEntityType, ChildTOType>
        extends BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildTOType> {

    protected OneToOneLink(final JpqlWhereBuilder jpqlWhereBuilder,
                               final JpqlOrderByBuilder jpqlOrderByBuilder,
                               @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize) {
        super(jpqlWhereBuilder, jpqlOrderByBuilder, batchLoadSize);
    }

    @Override
    public BatchLoaderWithContext<KeyType, ChildTOType> createLoader() {
        return createDataLoader((keys, children, dataFetchingEnvironment) -> {
            final Map<KeyType, ChildTOType> childrenMap = new HashMap<>();

            children.forEach(child -> childrenMap.put(createKeyFromChild(child), child));

            return childrenMap;
        });
    }
}

