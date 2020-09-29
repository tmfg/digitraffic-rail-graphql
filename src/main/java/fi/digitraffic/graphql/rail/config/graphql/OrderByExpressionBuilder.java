package fi.digitraffic.graphql.rail.config.graphql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathBuilder;

@Service
public class OrderByExpressionBuilder {
    public OrderSpecifier create(PathBuilder root, Map<String, Object> orderByAsMap) {
        Pair<List<String>, Object> order = this.getPathAndDeepValueAsString(orderByAsMap, new ArrayList<>());
        Path<Object> dynamicOrder = getProperty(root, order.getLeft());
        Order orderAsDSL = order.getRight().equals("ASCENDING") ? Order.ASC : Order.DESC;
        return new OrderSpecifier(orderAsDSL, dynamicOrder);
    }

    private Path<Object> getProperty(PathBuilder root, List<String> paths) {
        PathBuilder prop = root;
        for (String path : paths) {
            prop = prop.get(path);
        }

        return prop;
    }

    private Pair<List<String>, Object> getPathAndDeepValueAsString(Map rootValue, List<String> paths) {
        Set<Map.Entry> entries = rootValue.entrySet();
        Map.Entry entry = entries.iterator().next();
        Object value = entry.getValue();
        paths.add((String) entry.getKey());
        if (!entries.isEmpty() && value instanceof Map) {
            return getPathAndDeepValueAsString((Map) value, paths);
        } else {
            return Pair.of(paths, value);
        }
    }
}
