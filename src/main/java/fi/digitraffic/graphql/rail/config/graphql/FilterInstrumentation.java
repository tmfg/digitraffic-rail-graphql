package fi.digitraffic.graphql.rail.config.graphql;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.digitraffic.graphql.rail.filters.BaseFilter;
import fi.digitraffic.graphql.rail.filters.FilterRegistry;
import graphql.ExecutionResult;
import graphql.execution.ExecutionId;
import graphql.execution.ExecutionPath;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters;
import graphql.schema.GraphQLArgument;

@Component
public class FilterInstrumentation extends SimpleInstrumentation {
    private class FilteredExecutionPath {
        public ExecutionPath executionPath;
        public GraphQLArgument filterType;
        public Object filterTO;
    }

    private class SortedExecutionPath {
        public ExecutionPath executionPath;
        public Object orderBy;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SortedExecutionPath that = (SortedExecutionPath) o;
            return Objects.equals(executionPath, that.executionPath);
        }

        @Override
        public int hashCode() {
            return Objects.hash(executionPath);
        }
    }


    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Map<ExecutionId, List<FilteredExecutionPath>> filters = new HashMap<>();
    private Map<ExecutionId, Set<SortedExecutionPath>> collectionsToSort = new HashMap<>();
    private Map<ExecutionId, Map<Class, Object>> filterTOCache = new HashMap<>();

    @Autowired
    private FilterRegistry filterRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult, InstrumentationExecutionParameters parameters) {
        long start = System.currentTimeMillis();
        Map<String, List<Object>> executionResultData = executionResult.getData();

        List<ExecutionPath> filteredExecutionPaths = doFilter(parameters, executionResultData);
        removeFilteredRows(executionResultData, filteredExecutionPaths);

        log.info("Filtering took: {}", Duration.ofMillis(System.currentTimeMillis() - start));

        doSort(parameters, executionResultData);

        return super.instrumentExecutionResult(executionResult, parameters);
    }

    private void doSort(InstrumentationExecutionParameters parameters, Map<String, List<Object>> executionResultData) {
        Set<SortedExecutionPath> sortedExecutionPaths = this.collectionsToSort.get(parameters.getExecutionInput().getExecutionId());
        if (sortedExecutionPaths == null) {
            return;
        }
        for (SortedExecutionPath sortedExecutionPath : sortedExecutionPaths) {
            Object resultObject = executionResultData;
            for (Object o : sortedExecutionPath.executionPath.toList()) {
                if (o instanceof String) {
                    resultObject = ((Map) resultObject).get(o);
                } else if (o instanceof Integer) {
                    resultObject = ((List) resultObject).get((Integer) o);
                }
            }

            List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultObject;
            Map<String, String> orderByTo = (Map<String, String>) sortedExecutionPath.orderBy;
            Map.Entry<String, String> firstEntry = orderByTo.entrySet().iterator().next();
            String sortKey = firstEntry.getKey();
            String sortDirection = firstEntry.getValue();

            Collections.sort(resultList, (left, right) -> {
                Comparable leftProperty = (Comparable) left.get(sortKey);
                Comparable rightProperty = (Comparable) right.get(sortKey);
                if (sortDirection.equals("ASCENDING")) {
                    return leftProperty.compareTo(rightProperty);
                } else {
                    return rightProperty.compareTo(leftProperty);
                }
            });
        }
    }

    private List<ExecutionPath> doFilter(InstrumentationExecutionParameters parameters, Map<String, List<Object>> executionResultData) {
        ExecutionId executionId = parameters.getExecutionInput().getExecutionId();
        List<FilteredExecutionPath> filters = this.filters.get(executionId);
        List<ExecutionPath> filteredExecutionPaths = new ArrayList<>();
        if (filters != null) {
            for (FilteredExecutionPath filter : filters) {
                BaseFilter baseFilter = filterRegistry.getFilterFor(filter.filterType.getType().getName() + "TO");
                Object entityTO = objectMapper.convertValue(getExecutionResultDataByExecutionPath(executionResultData, filter.executionPath), baseFilter.getEntityTOType());
                Object filterTO = getFilterTO(executionId, filter.filterTO, baseFilter.getFilterTOType());

                if (baseFilter.isFiltered(entityTO, filterTO)) {
                    filteredExecutionPaths.add(filter.executionPath);
                }
            }
            Collections.reverse(filteredExecutionPaths);
        }
        return filteredExecutionPaths;
    }

    private void removeFilteredRows(Map<String, List<Object>> executionResultData, List<ExecutionPath> filteredExecutionPaths) {
        for (ExecutionPath filteredExecutionPath : filteredExecutionPaths) {
            Object resultObject = executionResultData;
            for (int i = 0; i < filteredExecutionPath.toList().size(); i++) {
                Object o = filteredExecutionPath.toList().get(i);
                if (i == filteredExecutionPath.toList().size() - 1) {
                    ((List) resultObject).remove((int) o);
                } else {
                    if (o instanceof String) {
                        resultObject = ((Map) resultObject).get(o);
                    } else if (o instanceof Integer) {
                        resultObject = ((List) resultObject).get((Integer) o);
                    }
                }
            }
        }
    }

    private Object getFilterTO(ExecutionId executionId, Object rawFilterTO, Class filterTOType) {
        Map<Class, Object> filterTOsByClass = this.filterTOCache.get(executionId);
        if (filterTOsByClass != null) {
            Object filterTO = filterTOsByClass.get(filterTOType);
            if (filterTO != null) {
                return filterTO;
            } else {
                filterTO = objectMapper.convertValue(rawFilterTO, filterTOType);
                filterTOsByClass.put(filterTOType, filterTO);
                return filterTO;
            }
        } else {
            filterTOsByClass = new HashMap<>();
            this.filterTOCache.put(executionId, filterTOsByClass);
            Object filterTO = objectMapper.convertValue(rawFilterTO, filterTOType);
            filterTOsByClass.put(filterTOType, filterTO);
            return filterTO;
        }
    }

    private Object getExecutionResultDataByExecutionPath(Map<String, List<Object>> executionResultData, ExecutionPath executionPath) {
        Object resultObject = executionResultData;
        for (Object o : executionPath.toList()) {
            if (o instanceof String) {
                resultObject = ((Map) resultObject).get(o);
            } else if (o instanceof Integer) {
                resultObject = ((List) resultObject).get((Integer) o);
            }
        }

        return resultObject;
    }

    @Override
    public ExecutionStrategyInstrumentationContext beginExecutionStrategy(InstrumentationExecutionStrategyParameters parameters) {
        ExecutionStepInfo executionStepInfo = parameters.getExecutionStrategyParameters().getExecutionStepInfo();
        Map<String, Object> arguments = executionStepInfo.getArguments();

        if (arguments == null || arguments.isEmpty()) {
            return super.beginExecutionStrategy(parameters);
        }

        ExecutionId executionId = parameters.getExecutionContext().getExecutionId();
        if (arguments.containsKey("where")) {
            List<FilteredExecutionPath> filteredExecutionPaths = initializeFilteredExecutionsList(executionId);

            FilteredExecutionPath filteredExecutionPath = new FilteredExecutionPath();
            filteredExecutionPath.executionPath = executionStepInfo.getPath();
            filteredExecutionPath.filterTO = arguments.get("where");
            filteredExecutionPath.filterType = executionStepInfo.getFieldDefinition().getArgument("where");

            filteredExecutionPaths.add(filteredExecutionPath);
        }
        if (arguments.containsKey("orderBy")) {
            Set<SortedExecutionPath> executionPaths = this.collectionsToSort.get(executionId);
            if (executionPaths == null) {
                executionPaths = new HashSet<>();
                this.collectionsToSort.put(executionId, executionPaths);
            }
            SortedExecutionPath sortedExecutionPath = new SortedExecutionPath();
            sortedExecutionPath.executionPath = executionStepInfo.getPath().getPathWithoutListEnd();
            sortedExecutionPath.orderBy = arguments.get("orderBy");

            executionPaths.add(sortedExecutionPath);
        }

        return super.beginExecutionStrategy(parameters);
    }

    private List<FilteredExecutionPath> initializeFilteredExecutionsList(ExecutionId executionId) {
        List<FilteredExecutionPath> filteredExecutionPaths = new ArrayList<>();
        List<FilteredExecutionPath> fileredExecutionsFromMapPaths = this.filters.get(executionId);
        if (fileredExecutionsFromMapPaths == null) {
            this.filters.put(executionId, filteredExecutionPaths);
        } else {
            filteredExecutionPaths = fileredExecutionsFromMapPaths;
        }
        return filteredExecutionPaths;
    }
}
