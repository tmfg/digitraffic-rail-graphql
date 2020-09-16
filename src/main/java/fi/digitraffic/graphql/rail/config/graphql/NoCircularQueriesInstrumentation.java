package fi.digitraffic.graphql.rail.config.graphql;

import static graphql.execution.instrumentation.SimpleInstrumentationContext.whenCompleted;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.analysis.QueryTraverser;
import graphql.analysis.QueryVisitorFieldEnvironment;
import graphql.analysis.QueryVisitorStub;
import graphql.execution.AbortExecutionException;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationValidationParameters;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.validation.ValidationError;

public class NoCircularQueriesInstrumentation extends SimpleInstrumentation {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public InstrumentationContext<List<ValidationError>> beginValidation(InstrumentationValidationParameters parameters) {
        return whenCompleted((errors, throwable) -> {
            if ((errors != null && errors.size() > 0) || throwable != null) {
                return;
            }
            QueryTraverser queryTraverser = newQueryTraverser(parameters);

            Map<String, Integer> typesSeenAtDepth = new HashMap<>();
            queryTraverser.visitPostOrder(new QueryVisitorStub() {
                @Override
                public void visitField(QueryVisitorFieldEnvironment env) {
                    String type;
                    GraphQLOutputType parentType = env.getParentType();
                    if (parentType instanceof GraphQLObjectType) {
                        type = parentType.getName();
                    } else {
                        type = parentType.getChildren().get(0).getName();
                    }
                    Integer depth = calculateDepthForField(env);

                    Integer typeLastDepth = typesSeenAtDepth.get(type);
                    if (typeLastDepth != null && typeLastDepth != depth) {
                        AbortExecutionException exception = new AbortExecutionException("Illegal query: " + type + " queried twice");
                        log.info(exception.toString());
                        throw exception;
                    } else if (typeLastDepth == null) {
                        typesSeenAtDepth.put(type, depth);
                    }
                }
            });
        });
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
