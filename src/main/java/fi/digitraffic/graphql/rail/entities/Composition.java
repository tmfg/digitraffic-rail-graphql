package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Composition {

    @EmbeddedId
    public TrainId id;

    public String operatorShortCode;
    public Integer operatorUicCode;

    public Long trainCategoryId;
    public Long trainTypeId;

    public Long version;

    @Column(updatable = false, insertable = false)
    public Long trainNumber;

    @Column(updatable = false, insertable = false)
    public LocalDate departureDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "departureDate", referencedColumnName = "departureDate", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "trainNumber", referencedColumnName = "trainNumber", nullable = false, insertable = false, updatable = false)})
    private Train train;

    @OneToMany(mappedBy = "composition", fetch = FetchType.LAZY)
    private Set<JourneySection> journeySections;

    protected Composition() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Composition)) return false;

        Composition that = (Composition) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
