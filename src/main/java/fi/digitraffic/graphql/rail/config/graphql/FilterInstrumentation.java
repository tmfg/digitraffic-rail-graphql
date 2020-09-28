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

import org.apache.commons.lang3.tuple.Pair;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FilteredExecutionPath that = (FilteredExecutionPath) o;
            return Objects.equals(executionPath, that.executionPath);
        }

        @Override
        public int hashCode() {
            return Objects.hash(executionPath);
        }
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

    private class LimitedExecutionPath {
        public ExecutionPath executionPath;
        public Object limit;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LimitedExecutionPath that = (LimitedExecutionPath) o;
            return Objects.equals(executionPath, that.executionPath);
        }

        @Override
        public int hashCode() {
            return Objects.hash(executionPath);
        }
    }

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Map<ExecutionId, Set<FilteredExecutionPath>> collectionsToFilter = new HashMap<>();
    private Map<ExecutionId, Set<SortedExecutionPath>> collectionsToSort = new HashMap<>();
    private Map<ExecutionId, Set<LimitedExecutionPath>> collectionsToLimit = new HashMap<>();

    @Autowired
    private FilterRegistry filterRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult, InstrumentationExecutionParameters parameters) {
        long start = System.currentTimeMillis();
        Map<String, List<Object>> executionResultData = executionResult.getData();

        doFilter(parameters, executionResultData);
        log.info("Filtering took: {}", Duration.ofMillis(System.currentTimeMillis() - start));
        doSort(parameters, executionResultData);
        doLimit(parameters, executionResultData);

        this.collectionsToFilter.remove(parameters.getExecutionInput().getExecutionId());
        this.collectionsToSort.remove(parameters.getExecutionInput().getExecutionId());
        this.collectionsToLimit.remove(parameters.getExecutionInput().getExecutionId());

        return super.instrumentExecutionResult(executionResult, parameters);
    }

    private void doLimit(InstrumentationExecutionParameters parameters, Map<String, List<Object>> executionResultData) {
        Set<LimitedExecutionPath> limitedExecutionPaths = this.collectionsToLimit.get(parameters.getExecutionInput().getExecutionId());
        if (limitedExecutionPaths == null) {
            return;
        }
        for (LimitedExecutionPath limitedExecutionPath : limitedExecutionPaths) {
            ExecutionPath executionPath = limitedExecutionPath.executionPath;
            Object resultObject = getObjectByExecutionPath(executionResultData, executionPath);

            ArrayList<Map<String, Object>> resultList = (ArrayList<Map<String, Object>>) resultObject;
            Integer limit = (Integer) limitedExecutionPath.limit;

            for (int i = resultList.size() - 1; i >= 0; i--) {
                if (i >= limit) {
                    resultList.remove(i);
                }
            }
        }
    }

    private Object getObjectByExecutionPath(Map<String, List<Object>> executionResultData, ExecutionPath executionPath) {
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

    private void doSort(InstrumentationExecutionParameters parameters, Map<String, List<Object>> executionResultData) {
        Set<SortedExecutionPath> sortedExecutionPaths = this.collectionsToSort.get(parameters.getExecutionInput().getExecutionId());
        if (sortedExecutionPaths == null) {
            return;
        }
        for (SortedExecutionPath sortedExecutionPath : sortedExecutionPaths) {
            Object resultObject = getObjectByExecutionPath(executionResultData, sortedExecutionPath.executionPath);

            List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultObject;
            Map<String, String> orderByTo = (Map<String, String>) sortedExecutionPath.orderBy;
            Pair<List<String>, String> deepValue = getPathAndDeepValueAsString(orderByTo, new ArrayList<>());
            List<String> paths = deepValue.getLeft();
            String sortDirection = deepValue.getRight();

            Collections.sort(resultList, (left, right) -> {
                Object leftProperty = null;
                Object rightProperty = null;
                for (String path : paths) {
                    leftProperty = leftProperty == null ? left.get(path) : ((Map) leftProperty).get(path);
                    rightProperty = rightProperty == null ? right.get(path) : ((Map) rightProperty).get(path);
                }
                if (leftProperty instanceof Comparable && rightProperty instanceof Comparable) {
                    if (sortDirection.equals("ASCENDING")) {
                        return ((Comparable) leftProperty).compareTo(rightProperty);
                    } else {
                        return ((Comparable) rightProperty).compareTo(leftProperty);
                    }
                } else {
                    throw new IllegalArgumentException("Sorting target is not a Comparable");
                }
            });
        }
    }

    public Pair<List<String>, String> getPathAndDeepValueAsString(Map rootValue, List<String> paths) {
        Set<Map.Entry> entries = rootValue.entrySet();
        Map.Entry entry = entries.iterator().next();
        Object value = entry.getValue();
        paths.add((String) entry.getKey());
        if (!entries.isEmpty() && value instanceof Map) {
            return getPathAndDeepValueAsString((Map) value, paths);
        } else {
            return Pair.of(paths, (String) value);
        }
    }

    private void doFilter(InstrumentationExecutionParameters parameters, Map<String, List<Object>> executionResultData) {
        ExecutionId executionId = parameters.getExecutionInput().getExecutionId();
        Set<FilteredExecutionPath> filters = this.collectionsToFilter.get(executionId);
        List<ExecutionPath> filteredExecutionPaths = new ArrayList<>();
        if (filters != null) {
            for (FilteredExecutionPath filter : filters) {
                BaseFilter baseFilter = filterRegistry.getFilterFor(filter.filterType.getType().getName() + "TO");
                List<Map<String, Object>> entityList = (List<Map<String, Object>>) getObjectByExecutionPath(executionResultData, filter.executionPath);
                Object filterTO = objectMapper.convertValue(filter.filterTO, baseFilter.getFilterTOType());
                for (int i = 0; i < entityList.size(); i++) {
                    Object entityTO = objectMapper.convertValue(entityList.get(i), baseFilter.getEntityTOType());
                    if (baseFilter.isFiltered(entityTO, filterTO)) {
                        filteredExecutionPaths.add(filter.executionPath.segment(i));
                    }
                }
            }
            Collections.reverse(filteredExecutionPaths);
        }

        removeFilteredRows(executionResultData, filteredExecutionPaths);
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

    @Override
    public ExecutionStrategyInstrumentationContext beginExecutionStrategy(InstrumentationExecutionStrategyParameters parameters) {
        ExecutionStepInfo executionStepInfo = parameters.getExecutionStrategyParameters().getExecutionStepInfo();
        Map<String, Object> arguments = executionStepInfo.getArguments();

        if (arguments == null || arguments.isEmpty()) {
            return super.beginExecutionStrategy(parameters);
        }

        ExecutionId executionId = parameters.getExecutionContext().getExecutionId();
        if (arguments.containsKey("where")) {
            Set<FilteredExecutionPath> filteredExecutionPaths = this.collectionsToFilter.get(executionId);
            if (filteredExecutionPaths == null) {
                filteredExecutionPaths = new HashSet<>();
                this.collectionsToFilter.put(executionId, filteredExecutionPaths);
            }

            FilteredExecutionPath filteredExecutionPath = new FilteredExecutionPath();
            filteredExecutionPath.executionPath = executionStepInfo.getPath().getPathWithoutListEnd();
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

        if (arguments.containsKey("take")) {
            Set<LimitedExecutionPath> limitedExecutionPaths = this.collectionsToLimit.get(executionId);
            if (limitedExecutionPaths == null) {
                limitedExecutionPaths = new HashSet<>();
                this.collectionsToLimit.put(executionId, limitedExecutionPaths);
            }
            LimitedExecutionPath limitedExecutionPath = new LimitedExecutionPath();
            limitedExecutionPath.executionPath = executionStepInfo.getPath().getPathWithoutListEnd();
            limitedExecutionPath.limit = arguments.get("take");

            limitedExecutionPaths.add(limitedExecutionPath);
        }

        return super.beginExecutionStrategy(parameters);
    }
}
