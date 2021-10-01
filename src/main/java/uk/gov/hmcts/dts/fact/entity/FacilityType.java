package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "admin_facilitytype")
@Getter
@Setter
public class FacilityType {

    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "search_facility_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    private String name;
    @Column(name = "name_cy")
    private String nameCy;
    private String image;
    @Column(name = "image_description")
    private String imageDescription;
    @Column(name = "image_file_path")
    private String imageFilePath;
    private Integer order;
}
