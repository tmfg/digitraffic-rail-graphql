package fi.digitraffic.graphql.rail.config.graphql;

import graphql.GraphQLError;
import graphql.execution.AbortExecutionException;
import graphql.schema.DataFetchingEnvironment;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.query.sqm.PathElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

@Component
public class ExceptionResolver extends DataFetcherExceptionResolverAdapter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String ERROR_IN_QUERY = "Error in query";

    @Override
    protected GraphQLError resolveToSingleError(final Throwable ex, final DataFetchingEnvironment env) {
        if(ex instanceof AbortExecutionException aee) {
            final var errorMessage = getErrorMessage(aee);

            return GraphQLError.newError().message(errorMessage).build();
        }

        return super.resolveToSingleError(ex, env);
    }

    private String getErrorMessage(final Exception ex) {
        final var pee = ExceptionUtils.throwableOfType(ex, PathElementException.class);

        log.error("method=resolveToSingleError Exception from query", ex);

        if(pee != null) {
            return pee.getMessage();
        }

        return ERROR_IN_QUERY;
    }
}
