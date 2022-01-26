package fi.digitraffic.graphql.rail.entities;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
