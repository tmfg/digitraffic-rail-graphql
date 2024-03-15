package fi.digitraffic.graphql.rail.to;

import com.querydsl.core.Tuple;

public abstract class BaseConverter<EntityTOTYpe> {
    public abstract EntityTOTYpe convert(final Tuple tuple);

    protected String nullableString(final Long value) {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    protected Integer nullableInt(final Long value) {
        if (value == null) {
            return null;
        } else {
            return value.intValue();
        }
    }
}
