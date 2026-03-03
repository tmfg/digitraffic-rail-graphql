package fi.digitraffic.graphql.rail.links.base.jpql;

import java.util.HashMap;
import java.util.Map;

import org.dataloader.BatchLoaderWithContext;

/**
 * JPQL-based implementation of OneToOneLink.
 * For relationships where each parent has exactly one child (e.g., TimeTableRow → Station).
 */
public abstract class OneToOneLinkJpql<KeyType, ParentTOType, ChildEntityType, ChildTOType>
        extends BaseLinkJpql<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildTOType> {

    @Override
    public BatchLoaderWithContext<KeyType, ChildTOType> createLoader() {
        return createDataLoader((keys, children, dataFetchingEnvironment) -> {
            final Map<KeyType, ChildTOType> childrenMap = new HashMap<>();

            children.forEach(child -> childrenMap.put(createKeyFromChild(child), child));

            return childrenMap;
        });
    }
}

