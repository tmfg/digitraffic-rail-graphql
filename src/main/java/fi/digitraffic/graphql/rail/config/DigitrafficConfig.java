package fi.digitraffic.graphql.rail.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("digitraffic")
public class DigitrafficConfig {
    private Set<String> hiddenFields;

    public Set<String> getHiddenFields() {
        return hiddenFields;
    }

    public void setHiddenFields(Set<String> hiddenFields) {
        this.hiddenFields = hiddenFields;
    }
}
