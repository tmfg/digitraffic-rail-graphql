package fi.digitraffic.graphql.rail.entities;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class PassengerInformationMessageId implements Serializable {
    public String id;
    public Integer version;

    public PassengerInformationMessageId(final String id, final int version) {
        this.id = id;
        this.version = version;
    }

    public PassengerInformationMessageId() {

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PassengerInformationMessageId)) {
            return false;
        }

        final PassengerInformationMessageId messageId = (PassengerInformationMessageId) o;

        if (!id.equals(messageId.id)) {
            return false;
        }
        if (!version.equals(messageId.version)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }
}
