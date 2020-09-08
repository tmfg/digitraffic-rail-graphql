package graphqlscope.graphql;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;
import graphqlscope.graphql.fetchers.MyDataFetcher;

@Component
@Internal
@Primary
@Profile({"request", "default"})
public class RequestScopedGraphQLInvocation implements GraphQLInvocation {
    @Autowired
    private List<MyDataFetcher> fetchers;

    private final GraphQL graphQL;
    private final GraphQLDataFetchers graphQLDataFetchers;

    public RequestScopedGraphQLInvocation(GraphQL graphQL, GraphQLDataFetchers graphQLDataFetchers) {
        this.graphQL = graphQL;
        this.graphQLDataFetchers = graphQLDataFetchers;
    }

    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest) {
        ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables());

        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();

        for (MyDataFetcher fetcher : fetchers) {
            DataLoader timeTableRowLoader = DataLoader.newDataLoader(fetcher.createLoader());
            dataLoaderRegistry.register(fetcher.getFieldName(), timeTableRowLoader);
        }

        executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
        executionInputBuilder.context(dataLoaderRegistry);

        ExecutionInput executionInput = executionInputBuilder.build();
        return graphQL.executeAsync(executionInput);
    }

}
