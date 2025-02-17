package fi.digitraffic.graphql.rail.links.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map backed by HashMap with ability to put multiple values with same key.
 * <p>
 * Put in
 * key value
 * K1   V1
 * K2   V2
 * K1   V11
 * <p>
 * Then getResults([K1, K1, K2]) will return [V1, V11, V2]
 *
 * @param <KeyType>
 * @param <ResultType>
 */
public class CountingKeyMap<KeyType, ResultType> {
    private final Map<KeyType, List<ResultType>> map;

    public CountingKeyMap(final int size) {
        this.map = new HashMap<KeyType, List<ResultType>>(size);
    }

    public void putAll(final Map<KeyType, ResultType> valueMap) {
        valueMap.forEach((key, value) -> {
            this.map.putIfAbsent(key, new ArrayList<>());

            this.map.get(key).add(value);
        });
    }

    public List<ResultType> getResults(final List<KeyType> keys) {
        final var newList = new ArrayList<ResultType>(keys.size());
        final var indexMap = new HashMap<KeyType, Integer>(keys.size());

        keys.forEach(key -> {
            final var index = indexMap.getOrDefault(key, 0);
            final var values = this.map.get(key);

            // get the value from correct index, or return null if no value found
            newList.add(values != null ? this.map.get(key).get(index) : null);

            indexMap.put(key, index + 1);
        });

        return newList;
    }
}
