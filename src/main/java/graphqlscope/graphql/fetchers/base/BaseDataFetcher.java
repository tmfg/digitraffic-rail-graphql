package graphqlscope.graphql.fetchers.base;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import graphql.schema.DataFetcher;

public abstract class BaseDataFetcher<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildFieldType> {
    @Autowired
    protected DataFetcherFactory dataFetcherFactory;

    public abstract String getTypeName();

    public abstract String getFieldName();

    public abstract KeyType createKeyFromParent(ParentTOType parent);

    public abstract KeyType createKeyFromChild(ChildEntityType child);

    public abstract ChildTOType createChildTOToFromChild(ChildEntityType child);

    public abstract List<ChildEntityType> findChildrenByKeys(List<KeyType> keys);

    public DataFetcher<CompletableFuture<ChildFieldType>> createFetcher() {
        return dataFetcherFactory.createDataFetcher(getFieldName(), parent -> createKeyFromParent((ParentTOType) parent));
    }
}
