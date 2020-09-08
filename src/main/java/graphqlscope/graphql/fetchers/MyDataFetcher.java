package graphqlscope.graphql.fetchers;

import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;

import graphql.schema.DataFetcher;

public abstract class MyDataFetcher<ParentId, Child> {
    public abstract String getTypeName();

    public abstract String getFieldName();

    public abstract DataFetcher<CompletableFuture<Child>> createFetcher();

    public abstract BatchLoader<ParentId, Child> createLoader();
}
