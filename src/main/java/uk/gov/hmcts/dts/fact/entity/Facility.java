package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "search_facility")
@Getter
public class Facility {
    @Id
    private Integer id;
    private String description;
    private String name;
}
