package graphqlscope.graphql.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Composition {

    @EmbeddedId
    public TrainId id;

    public String operatorShortCode;
    public Integer operatorUicCode;

    public Long trainCategoryId;
    public Long trainTypeId;

    public Long version;

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
