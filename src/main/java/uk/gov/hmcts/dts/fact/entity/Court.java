package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import static java.util.Comparator.comparing;

@Entity
@Table(name = "search_court")
@Getter
@Setter
@SuppressWarnings("PMD.TooManyFields")
public class Court {
    private static final String COURT_ID = "court_id";
    public static final String COURT_STRING = "court";
    @Id
    private Integer id;
    private String name;
    private String nameCy;
    private String slug;
    private String info;
    private String infoCy;
    private Boolean displayed;
    private String directions;
    private String directionsCy;
    private String imageFile;
    private Double lat;
    private Double lon;
    private String alert;
    private String alertCy;
    private Integer number;
    private Integer cciCode;
    private Integer magistrateCode;
    private Boolean hideAols;
    private Timestamp updatedAt;

    @ManyToMany
    @JoinTable(
        name = "search_courtareaoflaw",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "area_of_law_id")
    )
    private List<AreaOfLaw> areasOfLaw;

    @ManyToMany
    @JoinTable(
        name = "search_courtcourttype",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "court_type_id")
    )
    private List<CourtType> courtTypes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = COURT_STRING, orphanRemoval = true)
    @OrderBy("order")
    private List<CourtEmail> courtEmails;

    @ManyToMany
    @JoinTable(
        name = "search_courtcontact",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    @OrderBy("sort_order")
    private List<Contact> contacts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = COURT_STRING, orphanRemoval = true)
    @OrderBy("sort")
    private List<CourtOpeningTime> courtOpeningTimes;

    @ManyToMany
    @JoinTable(
        name = "search_courtfacility",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private List<Facility> facilities;

    @OneToMany(mappedBy = COURT_STRING)
    private List<CourtAddress> addresses;

    private String gbs;

    @OneToOne(mappedBy = "courtId")
    private InPerson inPerson;

    @ManyToMany
    @JoinTable(
        name = "search_serviceareacourt",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "servicearea_id")
    )
    private List<ServiceArea> serviceAreas;

    @OneToMany(mappedBy = COURT_STRING)
    private List<ServiceAreaCourt> serviceAreaCourts;

    public List<AreaOfLaw> getAreasOfLaw() {
        areasOfLaw.sort(comparing(AreaOfLaw::getName));
        return areasOfLaw;
    }
}
