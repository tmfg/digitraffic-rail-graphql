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

    protected int zeroIfNull(Integer number) {
        return number != null ? number : 0;
    }

    protected double zeroIfNull(Double number) {
        return number != null ? number : 0d;
    }

    protected long zeroIfNull(Long number) {
        return number != null ? number : 0L;
    }

    protected String emptyIfNull(Long number) {
        return number != null ? number.toString() : "";
    }

    protected boolean falseIfNull(Boolean bool) {
        return bool != null ? bool : false;
    }
}
