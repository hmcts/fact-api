package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "search_facility")
@Getter
@Setter
public class Facility {

    private static final String FACILITY_ID = "facility_id";

    @Id
    private Integer id;
    private String description;
    private String descriptionCy;
    private String name;
    private String nameCy;

    @ManyToOne
    @JoinTable(
        name = "search_facilityfacilitytype",
        joinColumns = @JoinColumn(name = FACILITY_ID),
        inverseJoinColumns = @JoinColumn(name = "facility_type_id")
    )
    private FacilityType facilityType;
}
