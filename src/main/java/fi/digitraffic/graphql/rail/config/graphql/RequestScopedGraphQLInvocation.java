package fi.digitraffic.graphql.rail.config.graphql;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import fi.digitraffic.graphql.rail.links.base.BaseLink;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;

@Component
@Internal
@Primary
public class RequestScopedGraphQLInvocation implements GraphQLInvocation {
    @Autowired
    private List<BaseLink> fetchers;

    @Autowired
    private GraphQL graphQL;

    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest) {
        ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables());

        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();

        for (BaseLink fetcher : fetchers) {
            BatchLoader<?, ?> dataLoader = fetcher.createLoader();
            DataLoader<?, ?> loader = DataLoader.newDataLoader(dataLoader);
            dataLoaderRegistry.register(fetcher.getTypeName() + "." + fetcher.getFieldName(), loader);
        }

        executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
        executionInputBuilder.context(dataLoaderRegistry);

        ExecutionInput executionInput = executionInputBuilder.build();

        return graphQL.executeAsync(executionInput);
    }

}
