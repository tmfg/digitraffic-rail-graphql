package fi.digitraffic.graphql.rail.to;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;

/**
 * Lightweight Tuple implementation for unit-testing convertProjection methods.
 * Provides named access to projection columns, matching Hibernate's Tuple contract.
 */
public class TestTuple implements Tuple {

    private final Map<String, Object> values;
    private final List<String> aliases;

    private TestTuple(final Map<String, Object> values) {
        this.values = values;
        this.aliases = List.copyOf(values.keySet());
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X get(final String alias, final Class<X> type) {
        if (!values.containsKey(alias)) {
            throw new IllegalArgumentException("Unknown tuple alias: " + alias);
        }
        return (X) values.get(alias);
    }

    @Override
    public Object get(final String alias) {
        if (!values.containsKey(alias)) {
            throw new IllegalArgumentException("Unknown tuple alias: " + alias);
        }
        return values.get(alias);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X get(final int i, final Class<X> type) {
        return (X) values.get(aliases.get(i));
    }

    @Override
    public Object get(final int i) {
        return values.get(aliases.get(i));
    }

    @Override
    public Object[] toArray() {
        return values.values().toArray();
    }

    @Override
    public List<TupleElement<?>> getElements() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <X> X get(final TupleElement<X> tupleElement) {
        return get(tupleElement.getAlias(), tupleElement.getJavaType());
    }

    public static class Builder {
        private final Map<String, Object> values = new LinkedHashMap<>();

        public Builder put(final String alias, final Object value) {
            values.put(alias, value);
            return this;
        }

        public TestTuple build() {
            return new TestTuple(values);
        }
    }
}
