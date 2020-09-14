package fi.digitraffic.graphql.rail.entities;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cause {
    @Id
    public Long id;

    @Embedded
    public TimeTableRowId timeTableRowId;
    public Long categoryCodeId;
    public Long detailedCategoryCodeId;
    public Long thirdCategoryCodeId;
}
