package fi.digitraffic.graphql.rail.links.base.jpql;

import java.util.HashMap;
import java.util.Map;

import org.dataloader.BatchLoaderWithContext;
import org.springframework.beans.factory.annotation.Value;

import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;

/**
 * JPQL-based implementation of OneToOneLink.
 * For relationships where each parent has exactly one child (e.g., TimeTableRow → Station).
 */
public abstract class OneToOneLinkJpql<KeyType, ParentTOType, ChildEntityType, ChildTOType>
        extends BaseLinkJpql<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildTOType> {

    protected OneToOneLinkJpql(final JpqlWhereBuilder jpqlWhereBuilder,
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

