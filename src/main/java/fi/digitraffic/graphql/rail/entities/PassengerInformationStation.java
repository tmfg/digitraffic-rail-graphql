package fi.digitraffic.graphql.rail.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rami_message_station")
public class PassengerInformationStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne(fetch = FetchType.LAZY)
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
    public String stationShortCode;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stationShortCode",
                referencedColumnName = "shortCode",
                updatable = false,
                insertable = false)
    public Station station;

    @Column(name = "rami_message_id")
    public String messageId;
    @Column(name = "rami_message_version")
    public Integer messageVersion;

    public PassengerInformationStation() {
    }

    public PassengerInformationStation(final PassengerInformationMessage message, final String stationShortCode) {
        this.message = message;
        this.stationShortCode = stationShortCode;
    }

    @Override
    public String toString() {
        return "PassengerInformationStation{" +
                "id=" + id +
                ", stationShortCode='" + stationShortCode + '\'' +
                '}';
    }
}
