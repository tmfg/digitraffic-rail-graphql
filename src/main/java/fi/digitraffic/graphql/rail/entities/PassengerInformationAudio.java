package fi.digitraffic.graphql.rail.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rami_message_audio")
public class PassengerInformationAudio {
    @Id
    public Long id;
    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "rami_message_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "rami_message_version", referencedColumnName = "version", nullable = false, insertable = false, updatable = false)})
    public PassengerInformationMessage message;
    public String fi;
    public String sv;
    public String en;
}
