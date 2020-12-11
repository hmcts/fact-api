package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import static java.util.Comparator.comparingInt;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.Utils.NAME_IS_DX;
import static uk.gov.hmcts.dts.fact.util.Utils.NAME_IS_NOT_DX;
import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;
import static uk.gov.hmcts.dts.fact.util.Utils.decodeUrlFromString;
import static uk.gov.hmcts.dts.fact.util.Utils.stripHtmlFromString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
@SuppressWarnings("PMD.TooManyFields")
@JsonPropertyOrder({"name", "slug", "info", "open", "directions", "image_file", "lat", "lon", "urgent_message",
    "crown_location_code", "county_location_code", "magistrates_location_code", "areas_of_law",
    "types", "emails", "contacts", "opening_times", "facilities", "addresses", "gbs", "dx_number", "service_area",
    "in_person"})
public class Court {
    private String name;
    private String slug;
    private String info;
    private Boolean open;
    private String directions;
    private String imageFile;
    private Double lat;
    private Double lon;
    @JsonProperty("urgent_message")
    private String alert;
    private Integer crownLocationCode;
    private Integer countyLocationCode;
    private Integer magistratesLocationCode;
    private List<AreaOfLaw> areasOfLaw;
    @JsonProperty("types")
    private List<String> courtTypes;
    private List<Email> emails;
    private List<Contact> contacts;
    private List<OpeningTime> openingTimes;
    private List<Facility> facilities;
    private List<CourtAddress> addresses;
    private String gbs;
    @JsonProperty("dx_number")
    private List<String> dxNumbers;
    @JsonProperty("service_area")
    private List<String> serviceAreas;
    private Boolean inPerson;
    private Boolean accessScheme;

    public Court(uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        this.name = chooseString(courtEntity.getNameCy(), courtEntity.getName());
        this.slug = courtEntity.getSlug();
        this.info = chooseString(courtEntity.getInfoCy(), courtEntity.getInfo());
        this.open = courtEntity.getDisplayed();
        this.directions = chooseString(courtEntity.getDirectionsCy(), courtEntity.getDirections());
        this.imageFile = courtEntity.getImageFile();
        this.lat = courtEntity.getLat();
        this.lon = courtEntity.getLon();
        this.alert = chooseString(courtEntity.getAlertCy(), courtEntity.getAlert());
        this.crownLocationCode = courtEntity.getNumber();
        this.countyLocationCode = courtEntity.getCciCode();
        this.magistratesLocationCode = courtEntity.getMagistrateCode();
        this.areasOfLaw = courtEntity
            .getAreasOfLaw()
            .stream()
            .map(areaOfLaw -> {
                AreaOfLaw areaOfLawObj = new AreaOfLaw(areaOfLaw);
                areaOfLawObj.setExternalLink(decodeUrlFromString(areaOfLawObj.getExternalLink()));
                return areaOfLawObj;
            })
            .collect(toList());
        this.contacts = courtEntity.getContacts().stream().filter(NAME_IS_NOT_DX)
            .map(Contact::new).collect(toList());
        this.courtTypes = courtEntity.getCourtTypes().stream().map(CourtType::getName).collect(toList());
        this.emails = courtEntity.getEmails().stream().map(Email::new).collect(toList());
        this.openingTimes = ofNullable(courtEntity.getCourtOpeningTimes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(cot -> cot.getOpeningTime())
            .map(OpeningTime::new).collect(toList());
        this.facilities = courtEntity
            .getFacilities()
            .stream()
            .map(facility -> {
                final Facility facilityObj = new Facility(facility);
                facilityObj.setDescription(stripHtmlFromString(facilityObj.getDescription()));
                return facilityObj;
            })
            .sorted(comparingInt(Facility::getOrder))
            .collect(toList());
        this.addresses = courtEntity.getAddresses().stream().map(CourtAddress::new).collect(toList());
        this.gbs = courtEntity.getGbs();
        this.dxNumbers = courtEntity.getContacts().stream().filter(NAME_IS_DX).map(uk.gov.hmcts.dts.fact.entity.Contact::getNumber)
            .collect(toList());
        this.serviceAreas = courtEntity.getServiceAreas() == null ? null : courtEntity.getServiceAreas()
            .stream()
            .map(ServiceArea::getName)
            .collect(toList());
        this.inPerson = courtEntity.getInPerson() == null || courtEntity.getInPerson().getIsInPerson();
        this.accessScheme = courtEntity.getInPerson() == null ? null : courtEntity.getInPerson().getAccessScheme();
    }
}
