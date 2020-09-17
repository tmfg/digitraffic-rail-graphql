package fi.digitraffic.graphql.rail.config.graphql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.digitraffic.graphql.rail.filters.BooleanFilter;
import graphql.ExecutionResult;
import graphql.execution.ExecutionId;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;

public class FilterInstrumentation extends SimpleInstrumentation {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Map<ExecutionId, Map<String, Object>> filterValue = new HashMap<>();
    private Map<ExecutionId, GraphQLArgument> filterType = new HashMap<>();

    @Override
    public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult, InstrumentationExecutionParameters parameters) {
        Map<String, List<HashMap<String, Object>>> executionResultData = executionResult.getData();
        for (Map.Entry<String, List<HashMap<String, Object>>> resultDataEntries : executionResultData.entrySet()) {
            if (!resultDataEntries.getKey().equals("__schema")) {
                List<Integer> filteredIndexes = new ArrayList<>();
                for (int i = 0; i < resultDataEntries.getValue().size(); i++) {
                    HashMap<String, Object> entityEntry = resultDataEntries.getValue().get(i);
                    ExecutionId executionId = parameters.getExecutionInput().getExecutionId();
                    for (Map.Entry<String, Object> filterEntry : filterValue.get(executionId).entrySet()) {
                        String fieldName = filterEntry.getKey();
                        GraphQLArgument graphQlFilterType = this.filterType.get(executionId);
                        if (graphQlFilterType.getType() instanceof GraphQLInputObjectType) {
                            GraphQLInputObjectField filterType = ((GraphQLInputObjectType) graphQlFilterType.getType()).getField(fieldName);
                            if (filterType.getType().getName().equals("BooleanFilter")) {
                                if (new BooleanFilter().filter(entityEntry, filterEntry)) {
                                    filteredIndexes.add(i);
                                }
                            }
                        }
                    }
                }
                Collections.reverse(filteredIndexes);
                for (int filteredIndex : filteredIndexes) {
                    resultDataEntries.getValue().remove(filteredIndex);
                }
            }
        }

        return super.instrumentExecutionResult(executionResult, parameters);
    }

    @Override
    public ExecutionStrategyInstrumentationContext beginExecutionStrategy(InstrumentationExecutionStrategyParameters parameters) {
        ExecutionStepInfo executionStepInfo = parameters.getExecutionStrategyParameters().getExecutionStepInfo();
        Map<String, Object> arguments = executionStepInfo.getArguments();
        if (arguments != null && !arguments.isEmpty() && arguments.containsKey("filter")) {
            this.filterType.put(parameters.getExecutionContext().getExecutionId(), executionStepInfo.getFieldDefinition().getArgument("filter"));
            this.filterValue.put(parameters.getExecutionContext().getExecutionId(), (Map<String, Object>) arguments.get("filter"));
        }
        return super.beginExecutionStrategy(parameters);
    }
}
