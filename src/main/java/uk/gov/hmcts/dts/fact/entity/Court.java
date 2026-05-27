package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;

@Entity
@Table(name = "search_court")
@Getter
@Setter
@SuppressWarnings("PMD.TooManyFields")
public class Court {
    private static final String COURT_ID = "court_id";
    private static final String COURT_STRING = "court";
    private static final String SORT = "sort";

    @Id
    @SequenceGenerator(name = "seq-court-gen", sequenceName = "public.search_court_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-court-gen")
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;
    @Column(name = "name_cy")
    private String nameCy;
    @Column(name = "slug")
    private String slug;
    @Column(name = "info")
    private String info;
    @Column(name = "info_cy")
    private String infoCy;
    @Column(name = "displayed")
    private Boolean displayed;
    @Column(name = "directions")
    private String directions;
    @Column(name = "directions_cy")
    private String directionsCy;
    @Column(name = "image_file")
    private String imageFile;
    @Column(name = "lat")
    private Double lat;
    @Column(name = "lon")
    private Double lon;
    @Column(name = "alert")
    private String alert;
    @Column(name = "alert_cy")
    private String alertCy;
    @Column(name = "number")
    private Integer number;
    @Column(name = "cci_code")
    private Integer cciCode;
    @Column(name = "magistrate_code")
    private Integer magistrateCode;
    @Column(name = "location_code")
    private Integer locationCode;
    @Column(name = "court_code")
    private Integer courtCode;
    @Column(name = "welsh_enabled")
    private Boolean welshEnabled;
    @Column(name = "hide_aols")
    private Boolean hideAols;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "region_id")
    private Integer regionId;

    @ManyToMany
    @JoinTable(
        name = "search_courtareaoflaw",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "area_of_law_id")
    )
    private List<AreaOfLaw> areasOfLaw;

    @OneToMany
    @JoinTable(
        name = "search_courtareaoflawspoe",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "area_of_law_id")
    )
    private List<AreaOfLaw> areasOfLawSpoe;

    @ManyToMany
    @JoinTable(
        name = "search_courtcourttype",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "court_type_id")
    )
    private List<CourtType> courtTypes;

    @OneToMany(mappedBy = COURT_STRING, orphanRemoval = true)
    @OrderBy("order")
    private List<CourtEmail> courtEmails;

    @OneToMany(mappedBy = COURT_STRING, orphanRemoval = true)
    @OrderBy("sort_order")
    private List<CourtContact> courtContacts;

    @OneToMany(mappedBy = COURT_STRING, orphanRemoval = true)
    @OrderBy(SORT)
    private List<CourtDxCode> courtDxCodes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = COURT_STRING, orphanRemoval = true)
    @OrderBy(SORT)
    private List<CourtOpeningTime> courtOpeningTimes;

    @OneToMany(mappedBy = COURT_STRING, orphanRemoval = true)
    @OrderBy(SORT)
    private List<CourtApplicationUpdate> courtApplicationUpdates;

    @ManyToMany
    @JoinTable(
        name = "search_courtfacility",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private List<Facility> facilities;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = COURT_STRING)
    @OrderBy("sort_order")
    private List<CourtAddress> addresses;

    @Column(name = "gbs")
    private String gbs;

    @OneToOne(cascade = {CascadeType.ALL}, mappedBy = "courtId")
    private InPerson inPerson;

    @OneToOne(cascade = {CascadeType.ALL}, mappedBy = "courtId")
    private ServiceCentre serviceCentre;

    @ManyToMany
    @JoinTable(
        name = "search_serviceareacourt",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "servicearea_id")
    )
    private List<ServiceArea> serviceAreas;

    @OneToMany(mappedBy = COURT_STRING)
    private List<ServiceAreaCourt> serviceAreaCourts;

    @OneToMany(mappedBy = COURT_STRING, orphanRemoval = true)
    private List<CourtPostcode> courtPostcodes;

    @OneToMany(mappedBy = COURT_STRING, orphanRemoval = true)
    @OrderBy(SORT)
    private List<CourtAdditionalLink> courtAdditionalLinks;

    public List<AreaOfLaw> getAreasOfLaw() {
        return ofNullable(areasOfLaw)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .sorted(comparing(AreaOfLaw::getName))
            .collect(Collectors.toList());
    }

    public List<String> getAreasOfLawSpoe() {
        return areasOfLawSpoe.stream().map(AreaOfLaw::getName).collect(Collectors.toList());
    }

    public boolean isInPerson() {
        // The in-person table does not contains all courts. So if a court does not exist in the in-person table (i.e. imPerson = null),
        // it will be considered an in-person court so the FaCT front-end can display the court page using relevant template.
        return inPerson == null || inPerson.getIsInPerson();
    }
}
