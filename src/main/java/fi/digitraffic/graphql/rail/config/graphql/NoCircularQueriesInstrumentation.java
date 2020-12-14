package fi.digitraffic.graphql.rail.config.graphql;

import static graphql.execution.instrumentation.SimpleInstrumentationContext.whenCompleted;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.digitraffic.graphql.rail.config.DigitrafficConfig;
import graphql.analysis.QueryTraverser;
import graphql.analysis.QueryVisitorFieldEnvironment;
import graphql.analysis.QueryVisitorStub;
import graphql.execution.AbortExecutionException;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationValidationParameters;
import graphql.language.Definition;
import graphql.language.OperationDefinition;
import graphql.validation.ValidationError;

public class NoCircularQueriesInstrumentation extends SimpleInstrumentation {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Set<String> ALLOWED_FIELDS;

    public NoCircularQueriesInstrumentation(DigitrafficConfig digitrafficConfig) {
        this.ALLOWED_FIELDS = digitrafficConfig.getFieldsThatCanBeQueriedTwice();
    }

    @Override
    public InstrumentationContext<List<ValidationError>> beginValidation(InstrumentationValidationParameters parameters) {
        return whenCompleted((errors, throwable) -> {
            if ((errors != null && errors.size() > 0) || throwable != null) {
                return;
            }

            if (isInstrospectionQuery(parameters)) {
                return;
            }
            QueryTraverser queryTraverser = newQueryTraverser(parameters);

            Map<String, Integer> typesSeenAtDepth = new HashMap<>();
            queryTraverser.visitPostOrder(new QueryVisitorStub() {
                @Override
                public void visitField(QueryVisitorFieldEnvironment env) {
                    Integer depth = calculateDepthForField(env);

                    String name = env.getField().getName();

                    Integer typeLastDepth = typesSeenAtDepth.get(name);
                    if (typeLastDepth != null &&
                            typeLastDepth != depth &&
                            env.getField().getSelectionSet() != null &&
                            !ALLOWED_FIELDS.contains(name)) {
                        AbortExecutionException exception = new AbortExecutionException("Illegal query: " + name + " queried twice");
                        log.info(exception.toString());
                        throw exception;
                    } else if (typeLastDepth == null) {
                        typesSeenAtDepth.put(name, depth);
                    }
                }
            });
        });
    }

    private boolean isInstrospectionQuery(InstrumentationValidationParameters parameters) {
        List<Definition> definitions = parameters.getDocument().getDefinitions().stream().filter(s -> s instanceof OperationDefinition).collect(Collectors.toList());
        if (definitions.size() == 1) {
            Definition firstDefinition = definitions.get(0);
            if (firstDefinition instanceof OperationDefinition) {
                String name = ((OperationDefinition) firstDefinition).getName();
                return name != null && name.equals("IntrospectionQuery");
            }
        }
        return false;
    }

    private Integer calculateDepthForField(QueryVisitorFieldEnvironment env) {
        int depth = 0;
        while (env.getParentEnvironment() != null) {
            depth++;
            env = env.getParentEnvironment();
        }

        return depth;
    }

    QueryTraverser newQueryTraverser(InstrumentationValidationParameters parameters) {
        return QueryTraverser.newQueryTraverser()
                .schema(parameters.getSchema())
                .document(parameters.getDocument())
                .operationName(parameters.getOperation())
                .variables(parameters.getVariables())
                .build();
    }


}
