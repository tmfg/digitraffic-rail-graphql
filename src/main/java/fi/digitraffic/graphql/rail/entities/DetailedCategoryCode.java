package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DetailedCategoryCode {
    @Id
    public Long id;
    public Long categoryCodeId;
    public String detailedCategoryCode;
    public String detailedCategoryName;
    public LocalDate validFrom;
    public LocalDate validTo;
}
