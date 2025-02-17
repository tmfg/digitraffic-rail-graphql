package fi.digitraffic.graphql.rail.links.base;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.dataloader.BatchLoaderWithContext;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;

public abstract class OneToOneLink<KeyType, ParentTOType, ChildEntityType, ChildTOType>
        extends BaseLink<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildTOType> {

    public BatchLoaderWithContext<KeyType, ChildTOType> createLoader() {
        final Function<JPAQueryFactory, JPAQuery<Tuple>> queryAfterFromFunction = (queryFactory) -> queryFactory.select(getFields()).from(getEntityTable());
        return doCreateLoader(queryAfterFromFunction);
    }

    public BatchLoaderWithContext<KeyType, ChildTOType> doCreateLoader(final Function<JPAQueryFactory, JPAQuery<Tuple>> queryAfterFromFunction) {
        return createDataLoader((children, dataFetchingEnvironment) -> {
                    final Map<KeyType, ChildTOType> childrenMap = new HashMap<>();

                    children.forEach(child -> childrenMap.put(createKeyFromChild(child), child));

                    return childrenMap;
                }
                , queryAfterFromFunction);
    }

}
