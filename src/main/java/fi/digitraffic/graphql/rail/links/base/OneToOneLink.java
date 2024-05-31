package fi.digitraffic.graphql.rail.links.base;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.dataloader.BatchLoaderWithContext;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;

public abstract class OneToOneLink<KeyType, ParentTOType, ChildEntityType, ChildTOType>
        extends BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildTOType> {

    public BatchLoaderWithContext<KeyType, ChildTOType> createLoader() {
        final JPAQuery<Tuple> queryAfterFrom = super.queryFactory.select(getFields()).from(getEntityTable());
        return doCreateLoader(queryAfterFrom);
    }

    public BatchLoaderWithContext<KeyType, ChildTOType> doCreateLoader(final JPAQuery<Tuple> queryAfterFrom) {
        return createDataLoader((children, dataFetchingEnvironment) -> {
                    final Map<KeyType, ChildTOType> childrenMap = new HashMap<>();
                    for (final ChildTOType child1 : children) {
                        final KeyType parentId = ((Function<ChildTOType, KeyType>) child -> createKeyFromChild(child)).apply(child1);
                        childrenMap.put(parentId, child1);
                    }

                    return childrenMap;
                }
                , queryAfterFrom);
    }

}
