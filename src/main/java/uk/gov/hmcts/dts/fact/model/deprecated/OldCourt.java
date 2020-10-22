package uk.gov.hmcts.dts.fact.model.deprecated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.model.Contact;
import uk.gov.hmcts.dts.fact.model.Email;
import uk.gov.hmcts.dts.fact.model.Facility;
import uk.gov.hmcts.dts.fact.model.OpeningTime;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
@JsonPropertyOrder({"name", "slug", "info", "open", "directions", "lat", "lon",
    "crown_location_code", "county_location_code", "magistrates_location_code", "areas_of_law",
    "types", "emails", "contacts", "opening_times", "facilities", "addresses", "gbs"})
public class OldCourt {
    private String name;
    private String slug;
    private String info;
    private Boolean open;
    private String directions;
    private Double lat;
    private Double lon;
    @JsonProperty("crown_location_code")
    private Integer crownLocationCode;
    @JsonProperty("county_location_code")
    private Integer countyLocationCode;
    @JsonProperty("magistrates_location_code")
    private Integer magistratesLocationCode;
    @JsonProperty("areas_of_law")
    private List<String> areasOfLaw;
    @JsonProperty("types")
    private List<String> courtTypes;
    private List<Email> emails;
    private List<Contact> contacts;
    @JsonProperty("opening_times")
    private List<OpeningTime> openingTimes;
    @JsonProperty("facilities")
    private List<Facility> facilities;
    private List<OldCourtAddress> addresses;
    private String gbs;

    public OldCourt(uk.gov.hmcts.dts.fact.entity.Court courtEntity, boolean welsh) {
        this.name = courtEntity.getName();
        this.slug = courtEntity.getSlug();
        this.info = courtEntity.getInfo();
        this.open = courtEntity.getDisplayed();
        this.directions = courtEntity.getDirections();
        this.lat = courtEntity.getLat();
        this.lon = courtEntity.getLon();
        this.crownLocationCode = courtEntity.getNumber();
        this.countyLocationCode = courtEntity.getCciCode();
        this.magistratesLocationCode = courtEntity.getMagistrateCode();
        this.areasOfLaw = courtEntity.getAreasOfLaw().stream().map(uk.gov.hmcts.dts.fact.entity.AreaOfLaw::getName)
            .collect(toList());
        this.courtTypes = courtEntity.getCourtTypes().stream().map(CourtType::getName).collect(toList());
        this.emails = courtEntity.getEmails().stream().map(e -> new Email(e, welsh)).collect(toList());
        this.contacts = courtEntity.getContacts().stream().map(c -> new Contact(c, welsh)).collect(toList());
        this.openingTimes = courtEntity.getOpeningTimes().stream().map(OpeningTime::new).collect(toList());
        this.facilities = courtEntity.getFacilities().stream().map(f -> new Facility(f, welsh)).collect(toList());
        this.addresses = courtEntity.getAddresses().stream().map(a -> new OldCourtAddress(a, welsh)).collect(toList());
        this.gbs = courtEntity.getGbs();
    }
}
