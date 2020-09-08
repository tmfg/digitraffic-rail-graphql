package graphqlscope.graphql.entities;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

@Entity
public class Cause {
    @Id
    public Long id;

    @Embedded
    public TimeTableRowId timeTableRowId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "departureDate", referencedColumnName = "departureDate", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "trainNumber", referencedColumnName = "trainNumber", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "attapId", referencedColumnName = "attapId", nullable = false, insertable = false, updatable = false)})
    public TimeTableRow timeTableRow;
}
