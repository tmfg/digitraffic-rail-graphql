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
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.SimplePerformantInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationCreateStateParameters;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.GraphQLNamedOutputType;

class ExecutionTimesByFieldState implements InstrumentationState {
    private final Map<String, Long> executionTimesByField = new HashMap<>();

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

public class ExecutionTimeInstrumentation extends SimplePerformantInstrumentation {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public InstrumentationState createState(final InstrumentationCreateStateParameters parameters) {
        return new ExecutionTimesByFieldState();
    }

    @Override
    public InstrumentationContext<Object> beginFieldFetch(final InstrumentationFieldFetchParameters parameters, final InstrumentationState state) {
        final long startNanos = System.nanoTime();
        return new SimpleInstrumentationContext<>() {
            @Override
            public void onCompleted(final Object result, final Throwable t) {
                final ExecutionStepInfo parent = parameters.getExecutionStepInfo().getParent();
                final String fieldTypeAndName;
                if (parent.getType() instanceof final GraphQLNamedOutputType parentType) {
                    fieldTypeAndName = parentType.getName() + "." + parameters.getField().getName();
                } else {
                    fieldTypeAndName = "null." + parameters.getField().getName();
                }
                if (state instanceof final ExecutionTimesByFieldState executionTimesByFieldState) {
                    executionTimesByFieldState.recordTiming(fieldTypeAndName, System.nanoTime() - startNanos);
                }
            }
        };
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(final InstrumentationExecutionParameters parameters,
                                                                  final InstrumentationState state) {
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
                final Duration duration = Duration.ofNanos(System.nanoTime() - startNanos);
                MDC.put("execution_time", String.valueOf(duration.toMillis()));

                log.debug("execution {} query {}", executionId, query);

                if (t != null) {
                    log.error(String.format("Exception in query %s", executionId), t);
                }

                if (!result.getErrors().isEmpty()) {
                    log.warn("Ending query {} tookMs={}. Details: {}", executionId, duration, state);
                    log.debug("errors: {}", result.getErrors());
                } else {
                    log.info("Ending query {} tookMs={}. Details: {}", executionId, duration, state);
                }
            }
        };
    }
}
