package fi.digitraffic.graphql.rail;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import fi.digitraffic.graphql.rail.fetchers.base.BaseDataFetcher;
import fi.digitraffic.graphql.rail.fetchers.base.OneToManyDataFetcher;
import fi.digitraffic.graphql.rail.fetchers.base.OneToOneDataFetcher;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;

@Component
@Internal
@Primary
@Profile({"request", "default"})
public class RequestScopedGraphQLInvocation implements GraphQLInvocation {
    @Autowired
    private List<BaseDataFetcher> fetchers;

    @Autowired
    private GraphQL graphQL;

    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest) {
        ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables());

        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();

        for (BaseDataFetcher fetcher : fetchers) {
            if (fetcher instanceof OneToOneDataFetcher) {
                DataLoader loader = DataLoader.newDataLoader(((OneToOneDataFetcher) fetcher).createLoader());
                dataLoaderRegistry.register(fetcher.getFieldName(), loader);
            } else if (fetcher instanceof OneToManyDataFetcher) {
                DataLoader timeTableRowLoader = DataLoader.newDataLoader(((OneToManyDataFetcher) fetcher).createLoader());
                dataLoaderRegistry.register(fetcher.getFieldName(), timeTableRowLoader);
            } else {
                throw new IllegalArgumentException("Loader was not of known type");
            }
        }

        executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
        executionInputBuilder.context(dataLoaderRegistry);

        ExecutionInput executionInput = executionInputBuilder.build();
        return graphQL.executeAsync(executionInput);
    }

}
