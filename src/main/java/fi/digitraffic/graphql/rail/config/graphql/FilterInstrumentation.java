package fi.digitraffic.graphql.rail.config.graphql;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.digitraffic.graphql.rail.filters.BaseFilter;
import fi.digitraffic.graphql.rail.filters.FilterRegistry;
import graphql.ExecutionResult;
import graphql.execution.ExecutionId;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters;
import graphql.schema.GraphQLArgument;

@Component
public class FilterInstrumentation extends SimpleInstrumentation {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Map<ExecutionId, Map<String, Object>> filterValue = new HashMap<>();
    private Map<ExecutionId, GraphQLArgument> filterType = new HashMap<>();

    @Autowired
    private FilterRegistry filterRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult, InstrumentationExecutionParameters parameters) {
        ExecutionId executionId = parameters.getExecutionInput().getExecutionId();

        Map<String, Object> filter = this.filterValue.get(executionId);
        GraphQLArgument filterType = this.filterType.get(executionId);

        doFiltering(executionResult, filter, filterType);

        return super.instrumentExecutionResult(executionResult, parameters);
    }

    private void doFiltering(ExecutionResult executionResult, Map<String, Object> filter, GraphQLArgument filterType) {
        if (filter != null) {
            long start = System.currentTimeMillis();
            BaseFilter baseFilter = filterRegistry.getFilterFor(filterType.getType());
            Object filterAsPOJO = objectMapper.convertValue(filter, baseFilter.getFilterTOType());

            Map<String, List<HashMap<String, Object>>> queryResult = executionResult.getData();
            for (Map.Entry<String, List<HashMap<String, Object>>> resultDataEntries : queryResult.entrySet()) {
                List<Integer> filteredIndexes = new ArrayList<>();
                List<HashMap<String, Object>> queryResultList = resultDataEntries.getValue();
                for (int i = 0; i < queryResultList.size(); i++) {
                    HashMap<String, Object> entity = queryResultList.get(i);

                    Object entityAsPOJO = objectMapper.convertValue(entity, baseFilter.getEntityTOType());
                    if (baseFilter.isFiltered(entityAsPOJO, filterAsPOJO)) {
                        filteredIndexes.add(i);
                    }
                }
                Collections.reverse(filteredIndexes);
                for (int filteredIndex : filteredIndexes) {
                    queryResultList.remove(filteredIndex);
                }

                log.info("Filtering took {}. Filtered entries: {}", Duration.ofMillis(System.currentTimeMillis() - start), filteredIndexes.size());
            }
        }
    }

    @Override
    public ExecutionStrategyInstrumentationContext beginExecutionStrategy(InstrumentationExecutionStrategyParameters parameters) {
        ExecutionStepInfo executionStepInfo = parameters.getExecutionStrategyParameters().getExecutionStepInfo();
        Map<String, Object> arguments = executionStepInfo.getArguments();
        if (arguments != null && !arguments.isEmpty() && arguments.containsKey("where")) {
            this.filterType.put(parameters.getExecutionContext().getExecutionId(), executionStepInfo.getFieldDefinition().getArgument("where"));
            this.filterValue.put(parameters.getExecutionContext().getExecutionId(), (Map<String, Object>) arguments.get("where"));
        }
        return super.beginExecutionStrategy(parameters);
    }
}
