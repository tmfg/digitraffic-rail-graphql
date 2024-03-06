package fi.digitraffic.graphql.rail.config.graphql;

import graphql.GraphQLError;
import graphql.execution.AbortExecutionException;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

@Component
public class ExceptionResolver extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(final Throwable ex, final DataFetchingEnvironment env) {
        if(ex instanceof AbortExecutionException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        return super.resolveToSingleError(ex, env);
    }
}
