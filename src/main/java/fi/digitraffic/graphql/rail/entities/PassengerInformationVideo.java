package fi.digitraffic.graphql.rail.entities;

import java.time.ZonedDateTime;
import java.util.List;

import fi.digitraffic.graphql.rail.util.DaysOfWeekConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
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
    @Column(name = "rami_message_id")
    public String messageId;
    @Column(name = "rami_message_version")
    public Integer messageVersion;
    public String textFi;
    public String textSv;
    public String textEn;

    public String deliveryType;
    public ZonedDateTime startDateTime;
    public ZonedDateTime endDateTime;
    public String startTime;
    public String endTime;
    @Column(name = "days_of_week")
    @Convert(converter = DaysOfWeekConverter.class)
    public List<String> weekDays;

    @Transient
    public PassengerInformationTextContent text;

    @Transient
    public PassengerInformationVideoDeliveryRules deliveryRules;

    @PostLoad
    public void setTransients() {
        this.text = new PassengerInformationTextContent(this.textFi, this.textSv, this.textEn);
        this.deliveryRules = new PassengerInformationVideoDeliveryRules(deliveryType, startDateTime, endDateTime, startTime, endTime, weekDays);
    }

}
