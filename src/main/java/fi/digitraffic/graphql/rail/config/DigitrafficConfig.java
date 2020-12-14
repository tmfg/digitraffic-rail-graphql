package fi.digitraffic.graphql.rail.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("digitraffic")
public class DigitrafficConfig {
    private Set<String> hiddenFields;
    private Set<String> fieldsThatCanBeQueriedTwice;

    public Set<String> getHiddenFields() {
        return hiddenFields;
    }

    public Set<String> getFieldsThatCanBeQueriedTwice() {
        return fieldsThatCanBeQueriedTwice;
    }

    public void setFieldsThatCanBeQueriedTwice(Set<String> fieldsThatCanBeQueriedTwice) {
        this.fieldsThatCanBeQueriedTwice = fieldsThatCanBeQueriedTwice;
    }

    public void setHiddenFields(Set<String> hiddenFields) {
        this.hiddenFields = hiddenFields;
    }
}
