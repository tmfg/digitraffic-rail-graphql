package fi.digitraffic.graphql.rail.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PassengerInformationMessageJoinId implements Serializable {
    @Column(name = "rami_message_id")
    private String id;

    @Column(name = "rami_message_version")
    private int version;

    public PassengerInformationMessageJoinId(final String id, final int version) {
        this.id = id;
        this.version = version;
    }

    public PassengerInformationMessageJoinId() {

    }
}
