package fi.digitraffic.graphql.rail.entities;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Cause {
    @Id
    public Long id;

    @Embedded
    public TimeTableRowId timeTableRowId;
    public String categoryCodeOid;
    public String detailedCategoryCodeOid;
    public String thirdCategoryCodeOid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryCodeOid", referencedColumnName = "oid", updatable = false, insertable = false)
    private CategoryCode categoryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detailedCategoryCodeOid", referencedColumnName = "oid", updatable = false, insertable = false)
    private DetailedCategoryCode detailedCategoryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thirdCategoryCodeOid", referencedColumnName = "oid", updatable = false, insertable = false)
    private ThirdCategoryCode thirdCategoryCode;
}
