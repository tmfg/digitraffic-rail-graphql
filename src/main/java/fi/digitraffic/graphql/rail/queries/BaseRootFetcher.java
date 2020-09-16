package fi.digitraffic.graphql.rail.queries;

import graphql.schema.DataFetcher;

public abstract class BaseRootFetcher<T> {
    public abstract String getQueryName();

    public abstract DataFetcher<T> createFetcher();
}
