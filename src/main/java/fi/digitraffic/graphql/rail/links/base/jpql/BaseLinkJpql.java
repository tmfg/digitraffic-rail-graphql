package fi.digitraffic.graphql.rail.links.base.jpql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.function.TriFunction;
import org.dataloader.BatchLoaderWithContext;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Lists;

import fi.digitraffic.graphql.rail.links.base.CountingKeyMap;
import fi.digitraffic.graphql.rail.links.base.MdcAwareThreadPoolExecutor;
import fi.digitraffic.graphql.rail.querydsl.JpqlOrderByBuilder;
import fi.digitraffic.graphql.rail.querydsl.JpqlWhereBuilder;
import graphql.execution.AbortExecutionException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.TypedQuery;

/**
 * Base class for JPQL-based graph-edge resolvers (links).
 */
public abstract class BaseLinkJpql<KeyType, ParentTOType, ChildEntityType, ChildTOType, ChildFieldType> {
    private static final ThreadPoolExecutor executor = new MdcAwareThreadPoolExecutor(20);
    private static final ThreadPoolExecutor sqlExecutor = new MdcAwareThreadPoolExecutor(10);

    private static final Logger log = LoggerFactory.getLogger(BaseLinkJpql.class);

    private final int batchLoadSize;
    protected final JpqlWhereBuilder jpqlWhereBuilder;
    protected final JpqlOrderByBuilder jpqlOrderByBuilder;

    @PersistenceContext
    protected EntityManager entityManager;

    protected BaseLinkJpql(final JpqlWhereBuilder jpqlWhereBuilder,
                           final JpqlOrderByBuilder jpqlOrderByBuilder,
                           @Value("${digitraffic.batch-load-size:500}") final int batchLoadSize) {
        this.jpqlWhereBuilder = jpqlWhereBuilder;
        this.jpqlOrderByBuilder = jpqlOrderByBuilder;
        this.batchLoadSize = batchLoadSize;
    }

    public boolean cachingEnabled() {
        return true;
    }

    public abstract String getTypeName();

    public abstract String getFieldName();

    public String createDataLoaderKey() {
        return this.getTypeName() + "." + this.getFieldName();
    }

    public abstract KeyType createKeyFromParent(final ParentTOType parent);

    public abstract KeyType createKeyFromChild(final ChildTOType child);

    public abstract ChildTOType createChildTOFromEntity(final ChildEntityType entity);

    public abstract BatchLoaderWithContext<KeyType, ChildFieldType> createLoader();

    public abstract Class<ChildEntityType> getEntityClass();

    /**
     * Returns the entity alias used in JPQL queries (e.g., "e" for "SELECT e FROM Station e")
     */
    public String getEntityAlias() {
        return "e";
    }

    /**
     * Builds the key-based WHERE clause and its named parameters.
     * <p>
     * For simple single-column keys use {@link #simpleInClause(String, List)}:
     * <pre>
     *     return simpleInClause("e.shortCode IN :keys", keys);
     * </pre>
     * For composite keys (e.g. {@link fi.digitraffic.graphql.rail.entities.TrainId}) delegate to
     * {@link TrainIdJpqlWhereClause#build}.
     */
    protected abstract KeyWhereClause buildKeyWhereClause(final List<KeyType> keys);

    protected static <T> KeyWhereClause simpleInClause(final String jpql, final List<T> keys) {
        return new KeyWhereClause(jpql, Map.of("keys", keys));
    }

    /**
     * Returns default ORDER BY clause or null if no default ordering.
     * Example: "e.name ASC"
     */
    public String getDefaultOrderBy() {
        return null;
    }


    public DataFetcher<CompletableFuture<ChildFieldType>> createFetcher() {
        return dataFetchingEnvironment -> {
            final ParentTOType parent = dataFetchingEnvironment.getSource();

            final DataLoaderRegistry dataLoaderRegistry = dataFetchingEnvironment.getDataLoaderRegistry();
            final DataLoader<KeyType, ChildFieldType> dataloader = dataLoaderRegistry.getDataLoader(getTypeName() + "." + getFieldName());

            return dataloader.load(createKeyFromParent(parent), dataFetchingEnvironment);
        };
    }

    private record LoaderBatch<KeyType>(List<KeyType> keys, DataFetchingEnvironment dfe) {}

    private List<LoaderBatch<KeyType>> createBatches(final List<KeyType> keys, final List<Object> keyContextsList) {
        final var batches = new ArrayList<LoaderBatch<KeyType>>();
        final var other = new ArrayList<LoaderBatch<KeyType>>();

        // make new batches for those that have alias
        // and collect all others to one list
        for (int i = 0; i < keys.size(); i++) {
            final var key = keys.get(i);
            final var dfe = (DataFetchingEnvironment) keyContextsList.get(i);

            if (dfe.getMergedField().getSingleField().getAlias() != null) {
                batches.add(new LoaderBatch<>(List.of(key), dfe));
            } else {
                if (other.isEmpty()) {
                    other.add(new LoaderBatch<>(new ArrayList<>(), dfe));
                }

                other.get(0).keys.add(key);
            }
        }

        // then partition the list with required size
        if (!other.isEmpty()) {
            final List<List<KeyType>> partitions = Lists.partition(other.get(0).keys, batchLoadSize);
            batches.addAll(partitions.stream().map(p -> new LoaderBatch<>(p, other.get(0).dfe)).toList());
        }

        return batches;
    }

    protected <ResultType> BatchLoaderWithContext<KeyType, ResultType> createDataLoader(
            final TriFunction<List<KeyType>, List<ChildTOType>, DataFetchingEnvironment, Map<KeyType, ResultType>> childGroupFunction) {
        return (keys, loaderContext) -> {
            final var batches = createBatches(keys, loaderContext.getKeyContextsList());

            return CompletableFuture.supplyAsync(() -> {
                final var resultMap = new CountingKeyMap<KeyType, ResultType>(keys.size());

                final List<Future<Map<KeyType, ResultType>>> futures = new ArrayList<>();

                for (final LoaderBatch<KeyType> batch : batches) {
                    MDC.put("execution_id", batch.dfe.getExecutionId().toString());

                    futures.add(sqlExecutor.submit(() -> {
                        final List<ChildEntityType> entities = executeQuery(
                                batch.keys,
                                batch.dfe.getArgument("where"),
                                batch.dfe.getArgument("orderBy")
                        );

                        final List<ChildTOType> children = entities.stream()
                                .map(this::createChildTOFromEntity)
                                .toList();

                        return childGroupFunction.apply(batch.keys, children, batch.dfe);
                    }));
                }

                for (final Future<Map<KeyType, ResultType>> future : futures) {
                    try {
                        final var map = future.get();
                        resultMap.putAll(map);
                    } catch (final QueryTimeoutException e) {
                        log.info("Timeout fetching children", e);
                        throw new AbortExecutionException(e);
                    } catch (final Exception e) {
                        log.error("Exception fetching children", e);
                        throw new AbortExecutionException(e);
                    }
                }

                return resultMap.getResults(keys);
            }, executor);
        };
    }

    /**
     * Executes the JPQL query with the given keys, where clause, and order by.
     */
    protected List<ChildEntityType> executeQuery(
            final List<KeyType> keys,
            final Map<String, Object> whereMap,
            final List<Map<String, Object>> orderByList) {

        final String alias = getEntityAlias();
        final Class<ChildEntityType> entityClass = getEntityClass();
        final String entityName = entityClass.getSimpleName();

        // Build base query
        final StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT ").append(alias)
                .append(" FROM ").append(entityName).append(" ").append(alias);

        // Build WHERE clause
        final var params = new java.util.HashMap<String, Object>();
        final List<String> whereClauses = new ArrayList<>();

        // Add key-based where clause
        final KeyWhereClause keyWhere = buildKeyWhereClause(keys);
        if (!keyWhere.jpql().isEmpty()) {
            whereClauses.add(keyWhere.jpql());
            params.putAll(keyWhere.params());
        }

        // Add user-provided where clause
        if (whereMap != null && !whereMap.isEmpty()) {
            final var whereResult = jpqlWhereBuilder.build(alias, replaceOffsetsWithZonedDateTimes(whereMap));
            if (!whereResult.jpql().isEmpty()) {
                whereClauses.add(whereResult.jpql());
                params.putAll(whereResult.params());
            }
        }

        if (!whereClauses.isEmpty()) {
            jpql.append(" WHERE ").append(String.join(" AND ", whereClauses));
        }

        // Build ORDER BY clause
        if (orderByList != null && !orderByList.isEmpty()) {
            final String orderByClause = jpqlOrderByBuilder.build(alias, orderByList);
            if (!orderByClause.isEmpty()) {
                jpql.append(" ORDER BY ").append(orderByClause);
            }
        } else {
            final String defaultOrderBy = getDefaultOrderBy();
            if (defaultOrderBy != null && !defaultOrderBy.isEmpty()) {
                jpql.append(" ORDER BY ").append(defaultOrderBy);
            }
        }

        // Execute query
        final TypedQuery<ChildEntityType> query = entityManager.createQuery(jpql.toString(), entityClass);
        params.forEach(query::setParameter);

        return query.getResultList();
    }

    /**
     * Converts OffsetDateTime to ZonedDateTime in the where map.
     */
    @SuppressWarnings("unchecked")
    protected static Map<String, Object> replaceOffsetsWithZonedDateTimes(final Map<String, Object> whereAsMap) {
        for (final String key : whereAsMap.keySet()) {
            final Object value = whereAsMap.get(key);
            if (value instanceof Map) {
                replaceOffsetsWithZonedDateTimes((Map<String, Object>) value);
            } else if (value instanceof final java.time.OffsetDateTime odt) {
                whereAsMap.put(key, odt.toZonedDateTime());
            }
        }
        return whereAsMap;
    }
}
