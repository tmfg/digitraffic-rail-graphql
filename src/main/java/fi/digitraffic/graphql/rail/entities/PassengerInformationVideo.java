package fi.digitraffic.graphql.rail.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "rami_message_video")
public class PassengerInformationVideo {
    @Id
    public Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "rami_message_id",
                        referencedColumnName = "id",
                        nullable = false,
                        insertable = false,
                        updatable = false),
            @JoinColumn(name = "rami_message_version",
                        referencedColumnName = "version",
                        nullable = false,
                        insertable = false,
                        updatable = false) })
    public PassengerInformationMessage message;
    public String textFi;
    public String textSv;
    public String textEn;

    @Transient
    public PassengerInformationTextContent text;

    public void setTextContent() {
        this.text = new PassengerInformationTextContent(this.textFi, this.textSv, this.textEn);
    }
}
