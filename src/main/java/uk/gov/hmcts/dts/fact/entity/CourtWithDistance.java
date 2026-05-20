package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Entity
@Table(name = "search_court")
@Getter
@Setter
@SuppressWarnings("PMD.TooManyFields")
public class CourtWithDistance {
    private static final String COURT_ID = "court_id";
    @Id
    @Column(name = "id")
    private Integer id;
    @Getter
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

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "number")
    private Integer number;

    @Column(name = "cci_code")
    private Integer cciCode;

    @Column(name = "magistrate_code")
    private Integer magistrateCode;

    @Column(name = "hide_aols")
    private Boolean hideAols;

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

    @ManyToMany
    @JoinTable(
        name = "search_courtemail",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "email_id")
    )
    private List<Email> emails;

    @ManyToMany
    @JoinTable(
        name = "search_courtcontact",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private List<Contact> contacts;

    @ManyToMany
    @JoinTable(
        name = "search_courtdxcode",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "dx_code_id")
    )
    private List<DxCode> dxCodes;

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

    @OneToMany
    @JoinTable(
        name = "search_courtaddress",
        joinColumns = @JoinColumn(name = COURT_ID),
        inverseJoinColumns = @JoinColumn(name = "id")
    )
    private List<CourtAddress> addresses;

    @Column(name = "gbs")
    private String gbs;

    @Column(name = "distance")
    private Double distance;

    public List<AreaOfLaw> getAreasOfLaw() {
        areasOfLaw.sort(comparing(AreaOfLaw::getName));
        return areasOfLaw;
    }

    public List<String> getAreasOfLawSpoe() {
        return areasOfLawSpoe.stream().map(AreaOfLaw::getName).collect(Collectors.toList());
    }
}
