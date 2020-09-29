package fi.digitraffic.graphql.rail.to;

import com.querydsl.core.Tuple;

public abstract class BaseConverter<EntityTOTYpe> {
    public abstract EntityTOTYpe convert(Tuple tuple);

    protected String nullableString(Long value) {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    protected Integer nullableInt(Long value) {
        if (value == null) {
            return null;
        } else {
            return value.intValue();
        }
    }
}
