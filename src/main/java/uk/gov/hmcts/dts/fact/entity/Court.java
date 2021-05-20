package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.*;

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

    @OneToMany(mappedBy = COURT_STRING, orphanRemoval = true)
    @OrderBy("order")
    private List<CourtEmail> courtEmails;

    @OneToMany(mappedBy = COURT_STRING, orphanRemoval = true)
    @OrderBy("sort_order")
    private List<CourtContact> courtContacts;

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
        return ofNullable(areasOfLaw)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .sorted(comparing(AreaOfLaw::getName))
            .collect(Collectors.toList());
    }
}
