package graphqlscope.graphql;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
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
import graphqlscope.graphql.entities.TimeTableRowId;
import graphqlscope.graphql.entities.TrainId;
import graphqlscope.graphql.model.CauseTO;
import graphqlscope.graphql.model.TimeTableRowTO;

@Component
@Internal
@Primary
@Profile({"request", "default"})
public class RequestScopedGraphQLInvocation implements GraphQLInvocation {

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
        DataLoader<TrainId, List<TimeTableRowTO>> timeTableRowLoader = DataLoader.newDataLoader(graphQLDataFetchers.timeTableRowBatchLoader());
        dataLoaderRegistry.register("timeTableRows", timeTableRowLoader);

        DataLoader<TimeTableRowId, List<CauseTO>> causeLoader = DataLoader.newDataLoader(graphQLDataFetchers.causeBatchLoader());
        dataLoaderRegistry.register("causes", causeLoader);

        executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
        executionInputBuilder.context(dataLoaderRegistry);

        ExecutionInput executionInput = executionInputBuilder.build();
        return graphQL.executeAsync(executionInput);
    }

}
