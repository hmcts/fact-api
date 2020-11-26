package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "admin_facilitytype")
@Getter
@Setter
public class FacilityType {

    @Id
    private Integer id;
    private String name;
    private Integer order;
}
