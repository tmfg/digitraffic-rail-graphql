package fi.digitraffic.graphql.rail.config;

import java.util.List;

import org.dataloader.BatchLoaderWithContext;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderOptions;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;

import fi.digitraffic.graphql.rail.links.base.BaseLink;
import reactor.core.publisher.Mono;

/**
 * Creates a new DataLoaderRegistry for each request
 */
@Component
public class RequestScopedInterceptor implements WebGraphQlInterceptor {

    @Autowired
    private List<BaseLink<?, ?, ?, ?, ?>> fetchers;

    private static final DataLoaderOptions CACHING_ENABLED = DataLoaderOptions.newOptions().build();
    private static final DataLoaderOptions CACHING_DISABLED = DataLoaderOptions.newOptions().setCachingEnabled(false).build();

    @Override
    public Mono<WebGraphQlResponse> intercept(final WebGraphQlRequest request, final WebGraphQlInterceptor.Chain chain) {
        request.configureExecutionInput((executionInput, builder) -> {
            final DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();

            for (final var fetcher : fetchers) {
                final BatchLoaderWithContext<?, ?> dataLoader = fetcher.createLoader();
                final DataLoaderOptions options = fetcher.cachingEnabled() ? CACHING_ENABLED : CACHING_DISABLED;
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