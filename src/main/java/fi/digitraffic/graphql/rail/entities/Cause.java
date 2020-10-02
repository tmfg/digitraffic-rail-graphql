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
    public Long categoryCodeId;
    public Long detailedCategoryCodeId;
    public Long thirdCategoryCodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryCodeId", referencedColumnName = "id", updatable = false, insertable = false)
    private CategoryCode categoryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detailedCategoryCode", referencedColumnName = "id", updatable = false, insertable = false)
    private DetailedCategoryCode detailedCategoryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thirdCategoryCodeId", referencedColumnName = "id", updatable = false, insertable = false)
    private ThirdCategoryCode thirdCategoryCode;
}
