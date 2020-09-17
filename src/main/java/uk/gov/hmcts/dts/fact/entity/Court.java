package uk.gov.hmcts.dts.fact.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;
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
@Data
@SuppressWarnings("PMD.TooManyFields")
@JsonPropertyOrder({"name", "slug", "info", "open", "directions", "lat", "lon",
        "crown_location_code", "county_location_code", "magistrates_location_code", "areas_of_law",
        "types", "emails", "contacts", "opening_times", "facilities", "addresses", "gbs"})
public class Court {
    private static final String COURT_ID = "court_id";
    @Id
    @JsonIgnore
    private Integer id;
    private String name;
    private String slug;
    private String info;
    @JsonProperty("open")
    private Boolean displayed;
    private String directions;
    private Double lat;
    private Double lon;
    @JsonProperty("crown_location_code")
    private Integer number;
    @JsonProperty("county_location_code")
    private Integer cciCode;
    @JsonProperty("magistrates_location_code")
    private Integer magistrateCode;

    @JsonProperty("areas_of_law")
    @ManyToMany
    @JoinTable(
            name = "search_courtareaoflaw",
            joinColumns = @JoinColumn(name = COURT_ID),
            inverseJoinColumns = @JoinColumn(name = "area_of_law_id")
    )
    private List<AreaOfLaw> areasOfLaw;

    @JsonProperty("types")
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
    @JsonProperty("opening_times")
    private List<OpeningTime> openingTimes;

    @ManyToMany
    @JoinTable(
            name = "search_courtfacility",
            joinColumns = @JoinColumn(name = COURT_ID),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    @JsonProperty("facilities")
    private List<Facility> facilities;

    @OneToMany(mappedBy = "court")
    private List<CourtAddress> addresses;

    private String gbs;

    public List<AreaOfLaw> getAreasOfLaw() {
        areasOfLaw.sort(comparing(AreaOfLaw::getName));
        return areasOfLaw;
    }
}
