package graphqlscope.graphql.fetchers;

import org.dataloader.BatchLoader;

import graphql.schema.DataFetcher;

public abstract class MyDataFetcher {
    public abstract String getTypeName();

    public abstract String getFieldName();

    public abstract DataFetcher createFetcher();

    public abstract BatchLoader createLoader();
}
