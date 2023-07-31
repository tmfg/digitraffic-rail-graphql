package fi.digitraffic.graphql.rail.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CategoryCode {
    @Id
    public String oid;
    @Column(name = "category_code")
    public String code;
    @Column(name = "category_name")
    public String name;
    public LocalDate validFrom;
    public LocalDate validTo;
}
