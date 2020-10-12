package uk.gov.hmcts.dts.fact.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_facility")
@Data
public class Facility {
    @Id
    private Integer id;
    private String description;
    private String name;
}
