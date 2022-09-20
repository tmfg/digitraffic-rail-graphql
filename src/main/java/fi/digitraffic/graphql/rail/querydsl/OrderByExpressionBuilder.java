package fi.digitraffic.graphql.rail.querydsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathBuilder;

@Service
public class OrderByExpressionBuilder {
    public List<OrderSpecifier> create(PathBuilder root, List<Map<String, Object>> orderByArgument) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        for (Map<String, Object> orderByMap : orderByArgument) {
            Pair<List<String>, Object> order = this.getPathAndDeepValueAsString(orderByMap, new ArrayList<>());
            Path<Object> dynamicOrder = getProperty(root, order.getFirst());
            Order orderAsDSL = order.getSecond().equals("ASCENDING") ? Order.ASC : Order.DESC;
            orderSpecifiers.add(new OrderSpecifier(orderAsDSL, dynamicOrder));
        }

        return orderSpecifiers;
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
