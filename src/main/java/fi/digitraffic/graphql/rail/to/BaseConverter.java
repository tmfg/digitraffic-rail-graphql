package fi.digitraffic.graphql.rail.to;

public abstract class BaseConverter {

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
