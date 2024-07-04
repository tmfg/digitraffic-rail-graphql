package fi.digitraffic.graphql.rail.entities;

import java.time.ZonedDateTime;
import java.util.List;

import fi.digitraffic.graphql.rail.util.DaysOfWeekConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "rami_message_audio")
public class PassengerInformationAudio {
    @Id
    public Long id;
    @OneToOne
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

    public String deliveryType;
    public String eventType;
    public ZonedDateTime startDateTime;
    public ZonedDateTime endDateTime;
    public String startTime;
    public String endTime;
    @Column(name = "days_of_week")
    @Convert(converter = DaysOfWeekConverter.class)
    public List<String> weekDays;
    public ZonedDateTime deliveryAt;
    public Integer repetitions;
    public Integer repeatEvery;

    @Transient
    public PassengerInformationTextContent text;

    @Transient
    public PassengerInformationAudioDeliveryRules deliveryRules;

    @PostLoad
    public void setTransients() {
        this.text = new PassengerInformationTextContent(this.textFi, this.textSv, this.textEn);
        this.deliveryRules =
                new PassengerInformationAudioDeliveryRules(this.deliveryType, this.eventType, this.startDateTime, this.endDateTime, this.startTime,
                        this.endTime, this.weekDays, this.deliveryAt, this.repetitions != null ? this.repetitions : null,
                        this.repeatEvery != null ? this.repeatEvery : null);
    }

}
