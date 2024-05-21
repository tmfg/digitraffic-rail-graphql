package fi.digitraffic.graphql.rail.entities;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class PassengerInformationMessageId implements Serializable {
    public String id;
    public int version;

    public PassengerInformationMessageId(final String id, final int version) {
        this.id = id;
        this.version = version;
    }

    public PassengerInformationMessageId() {
        
    }
}
