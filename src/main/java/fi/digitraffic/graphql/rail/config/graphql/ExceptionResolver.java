package fi.digitraffic.graphql.rail.config.graphql;

import graphql.GraphQLError;
import graphql.execution.AbortExecutionException;
import graphql.schema.DataFetchingEnvironment;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.HibernateException;
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
            if(ExceptionUtils.indexOfType(aee, HibernateException.class) > -1) {
                log.error("method=resolveToSingleError Exception from query", ex);

                return GraphQLError.newError().message(ERROR_IN_QUERY).build();
            }

            final var errorMessage = ObjectUtils.firstNonNull(ex.getMessage(), ERROR_IN_QUERY);

            return GraphQLError.newError().message(errorMessage).build();
        }

        return super.resolveToSingleError(ex, env);
    }
}
