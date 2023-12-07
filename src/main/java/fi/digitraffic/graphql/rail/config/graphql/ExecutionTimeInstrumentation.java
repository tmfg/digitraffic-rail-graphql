package fi.digitraffic.graphql.rail.config.graphql;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import graphql.ExecutionResult;
import graphql.execution.ExecutionId;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldParameters;
import graphql.schema.GraphQLNamedOutputType;

class ExecutionTimesByFieldState implements InstrumentationState {
    private Map<String, Long> executionTimesByField = new HashMap<>();

    void recordTiming(final String key, final Long time) {
        executionTimesByField.put(key, time);
    }

    @Override
    public String toString() {
        return executionTimesByField.entrySet().stream()
                .map(e -> e.getKey() + "=" + Duration.ofNanos(e.getValue()))
                .collect(Collectors.joining("\n"));
    }
}

public class ExecutionTimeInstrumentation extends SimpleInstrumentation {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public InstrumentationState createState() {
        return new ExecutionTimesByFieldState();
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginField(final InstrumentationFieldParameters parameters) {
        final long startNanos = System.nanoTime();
        return new SimpleInstrumentationContext<>() {
            @Override
            public void onCompleted(final ExecutionResult result, final Throwable t) {
                final ExecutionTimesByFieldState state = parameters.getInstrumentationState();
                final ExecutionStepInfo parent = parameters.getExecutionStepInfo().getParent();

                final String fieldTypeAndName;
                if (parent.getType() instanceof GraphQLNamedOutputType) {
                    final GraphQLNamedOutputType parentType = (GraphQLNamedOutputType) parent.getType();
                    fieldTypeAndName = parentType.getName() + "." + parameters.getField().getName();
                } else {
                    fieldTypeAndName = "null." + parameters.getField().getName();
                }

                state.recordTiming(fieldTypeAndName, System.nanoTime() - startNanos);
            }
        };
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(final InstrumentationExecutionParameters parameters) {
        final ExecutionId executionId = parameters.getExecutionInput().getExecutionId();
        final String query = parameters.getQuery();
        MDC.put("query_hashcode", String.valueOf(query.hashCode()));
        MDC.put("execution_id", executionId.toString());
        MDC.remove("execution_time");
        // log.info("Starting query {} {}", executionId, query);

        final long startNanos = System.nanoTime();
        return new SimpleInstrumentationContext<>() {
            @Override
            public void onCompleted(final ExecutionResult result, final Throwable t) {
                if (t != null) {
                    log.error(String.format("Exception in query %s %s", executionId, query), t);
                }

                final ExecutionTimesByFieldState state = parameters.getInstrumentationState();
                final Duration duration = Duration.ofNanos(System.nanoTime() - startNanos);

                if (!result.getErrors().isEmpty()) {
                    log.info("Ending query {} {} took {}. Details: {}, Errors: {}", executionId, query, duration, state, result.getErrors());
                } else {
                    log.info("Ending query {} {} took {}. Details: {}", executionId, query, duration, state);
                }
                MDC.put("execution_time", String.valueOf(duration.toMillis()));
            }
        };
    }
}
