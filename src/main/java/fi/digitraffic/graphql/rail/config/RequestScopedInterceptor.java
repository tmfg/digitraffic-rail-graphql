package fi.digitraffic.graphql.rail.config;

import fi.digitraffic.graphql.rail.links.base.BaseLink;
import org.dataloader.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;

import reactor.core.publisher.Mono;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Creates a new DataLoaderRegistry for each request
 */
@Component
public class RequestScopedInterceptor implements WebGraphQlInterceptor {

    @Autowired
    private List<BaseLink> fetchers;

    @Override
    public Mono<WebGraphQlResponse> intercept(final WebGraphQlRequest request, final WebGraphQlInterceptor.Chain chain) {
        request.configureExecutionInput((executionInput, builder) -> {
            final DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
            final var options = DataLoaderOptions.newOptions().setCachingEnabled(false);

            for (final var fetcher : fetchers) {
                final BatchLoaderWithContext<?, ?> dataLoader = fetcher.createLoader();
                final DataLoader<?, ?> loader = DataLoaderFactory.newDataLoader(dataLoader, options);
                dataLoaderRegistry.register(fetcher.createDataLoaderKey(), loader);
            }
            return builder
                    .dataLoaderRegistry(dataLoaderRegistry)
                    .build();
        });

        return chain.next(request);
    }
}