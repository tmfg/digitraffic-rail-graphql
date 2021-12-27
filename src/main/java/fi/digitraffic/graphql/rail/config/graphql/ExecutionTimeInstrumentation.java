package fi.digitraffic.graphql.rail.config.graphql;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import graphql.ExecutionResult;
import graphql.execution.ExecutionId;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldParameters;

class ExecutionTimesByFieldState implements InstrumentationState {
    private Map<String, Long> executionTimesByField = new HashMap<>();

    void recordTiming(String key, Long time) {
        executionTimesByField.put(key, time);
    }

    @Override
    public String toString() {
        String output = "";
        for (Map.Entry<String, Long> stringObjectEntry : executionTimesByField.entrySet()) {
            output += stringObjectEntry.getKey() + "=" + Duration.ofNanos(stringObjectEntry.getValue()) + "\n";
        }
        return output;
    }
}

public class ExecutionTimeInstrumentation extends SimpleInstrumentation {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public InstrumentationState createState() {
        return new ExecutionTimesByFieldState();
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginField(InstrumentationFieldParameters parameters) {
        long startNanos = System.nanoTime();
        return new SimpleInstrumentationContext<>() {
            @Override
            public void onCompleted(ExecutionResult result, Throwable t) {
                ExecutionTimesByFieldState state = parameters.getInstrumentationState();
                String fieldTypeAndName = parameters.getExecutionStepInfo().getParent().getType().getName() + "." + parameters.getField().getName();
                state.recordTiming(fieldTypeAndName, System.nanoTime() - startNanos);
            }
        };
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
        final ExecutionId executionId = parameters.getExecutionInput().getExecutionId();
        final String query = parameters.getQuery();
        MDC.put("query_hashcode",  String.valueOf(query.hashCode()));
        MDC.put("execution_id", executionId.toString());
        MDC.remove("execution_time");
        log.info("Starting query {} {}", executionId, query);

        long startNanos = System.nanoTime();
        return new SimpleInstrumentationContext<>() {
            @Override
            public void onCompleted(ExecutionResult result, Throwable t) {
                ExecutionTimesByFieldState state = parameters.getInstrumentationState();

                Duration duration = Duration.ofNanos(System.nanoTime() - startNanos);
                log.info("Ending query {} {} took {}. Details: {}", executionId, query, duration, state);
                MDC.put("execution_time", String.valueOf(duration.toMillis()));
            }
        };
    }
}
