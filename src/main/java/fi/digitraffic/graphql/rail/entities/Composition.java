package fi.digitraffic.graphql.rail.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import org.springframework.context.annotation.Lazy;

@Entity
public class Composition {

    @EmbeddedId
    public TrainId id;

    public String operatorShortCode;
    public Integer operatorUicCode;

    public Long trainCategoryId;
    public Long trainTypeId;

    public Long version;

    @Lazy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "departureDate", referencedColumnName = "departureDate", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "trainNumber", referencedColumnName = "trainNumber", nullable = false, insertable = false, updatable = false)})
    private Train train;

//    @OneToMany(mappedBy = "composition", fetch = FetchType.LAZY)
//    @OrderBy
//    public Set<JourneySection> journeySections = new LinkedHashSet<>();

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
