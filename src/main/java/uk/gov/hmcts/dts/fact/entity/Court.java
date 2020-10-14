package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import static java.util.Comparator.comparing;

@Entity
@Table(name = "search_court")
@Getter
@SuppressWarnings("PMD.TooManyFields")
public class Court {
    private static final String COURT_ID = "court_id";
    @Id
    private Integer id;
    private String name;
    private String slug;
    private String info;
    private Boolean displayed;
    private String directions;
    private Double lat;
    private Double lon;
    private Integer number;
    private Integer cciCode;
    private Integer magistrateCode;
    private Boolean hideAols;

    @OneToMany(mappedBy = "court")
    private List<CourtAreaOfLaw> courtAreaOfLaw;

    @ManyToMany
    @JoinTable(
        name = "search_courtcourttype",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "court_type_id")
    )
    private List<CourtType> courtTypes;

    @ManyToMany
    @JoinTable(
        name = "search_courtemail",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "email_id")
    )
    private List<CourtEmail> emails;

    @ManyToMany
    @JoinTable(
        name = "search_courtcontact",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private List<Contact> contacts;

    @ManyToMany
    @JoinTable(
        name = "search_courtopeningtime",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "opening_time_id")
    )
    private List<OpeningTime> openingTimes;

    @ManyToMany
    @JoinTable(
        name = "search_courtfacility",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private List<Facility> facilities;

    @OneToMany(mappedBy = "court")
    private List<CourtAddress> addresses;

    private String gbs;

    public List<AreaOfLaw> getAreasOfLaw() {
        return courtAreaOfLaw
            .stream()
            .map(c -> c.getAreaOfLaw())
            .sorted(comparing(AreaOfLaw::getName))
            .collect(Collectors.toList());
    }
}
