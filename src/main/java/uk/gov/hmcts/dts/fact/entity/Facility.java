package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
