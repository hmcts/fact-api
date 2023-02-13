package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "search_facility")
@Getter
@Setter
@NoArgsConstructor
public class Facility {

    @ManyToMany(mappedBy = "facilities")
    private List<Court> courts;

    private static final String FACILITY_ID = "facility_id";

    @Id
    @SequenceGenerator(name = "seq-gen-facility", sequenceName = "search_facility_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen-facility")
    private Integer id;
    private String description;
    @Column(name = "description_cy")
    private String descriptionCy;
    private String name;
    @Column(name = "name_cy")
    private String nameCy;
    private String image;
    @Column(name = "image_description")
    private String imageDescription;
    @Column(name = "image_file_path")
    private String imageFilePath;

    @ManyToOne
    @JoinTable(
        name = "search_facilityfacilitytype",
        joinColumns = @JoinColumn(name = FACILITY_ID),
        inverseJoinColumns = @JoinColumn(name = "facility_type_id")
    )
    private FacilityType facilityType;

    public Facility(final String description, final String descriptionCy,final FacilityType facilityType) {
        this.name = facilityType.getName();
        this.nameCy = facilityType.getNameCy();
        this.description = description;
        this.descriptionCy = descriptionCy;
        this.image = facilityType.getImage();
        this.imageDescription = facilityType.getImageDescription();
        this.imageFilePath = facilityType.getImageFilePath();
        this.facilityType = facilityType;
    }
}
