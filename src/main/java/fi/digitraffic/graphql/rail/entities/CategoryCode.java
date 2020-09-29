package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CategoryCode {
    @Id
    public Long id;
    @Column(name = "category_code")
    public String code;
    @Column(name = "category_name")
    public String name;
    public LocalDate validFrom;
    public LocalDate validTo;
}
