package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.util.Filters;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    @JsonProperty("image_file")
    private String imageFile;
    private Double lat;
    private Double lon;
    @JsonProperty("urgent_message")
    private String alert;
    @JsonProperty("crown_location_code")
    private Integer crownLocationCode;
    @JsonProperty("county_location_code")
    private Integer countyLocationCode;
    @JsonProperty("magistrates_location_code")
    private Integer magistratesLocationCode;
    @JsonProperty("areas_of_law")
    private List<AreaOfLaw> areasOfLaw;
    @JsonProperty("types")
    private List<String> courtTypes;
    private List<Email> emails;
    private List<Contact> contacts;
    @JsonProperty("opening_times")
    private List<OpeningTime> openingTimes;
    @JsonProperty("facilities")
    private List<Facility> facilities;
    private List<CourtAddress> addresses;
    private String gbs;
    @JsonProperty("dx_number")
    private List<String> dxNumbers;
    @JsonProperty("service_area")
    private String serviceArea;
    @JsonProperty("in_person")
    private Boolean inPerson;

    public Court(uk.gov.hmcts.dts.fact.entity.Court courtEntity, boolean welsh) {
        this.name = welsh ? courtEntity.getNameCy() : courtEntity.getName();
        this.slug = courtEntity.getSlug();
        this.info = Filters.stripHtmlFromString(welsh ? courtEntity.getInfoCy() : courtEntity.getInfo());
        this.open = courtEntity.getDisplayed();
        this.directions = welsh ? courtEntity.getDirectionsCy() : courtEntity.getDirections();
        this.imageFile = courtEntity.getImageFile();
        this.lat = courtEntity.getLat();
        this.lon = courtEntity.getLon();
        this.alert = welsh ? courtEntity.getAlertCy() : courtEntity.getAlert();
        this.crownLocationCode = courtEntity.getNumber();
        this.countyLocationCode = courtEntity.getCciCode();
        this.magistratesLocationCode = courtEntity.getMagistrateCode();
        this.areasOfLaw = courtEntity.getAreasOfLaw().stream().map(aol -> new AreaOfLaw(aol, welsh)).collect(toList());
        this.contacts = courtEntity.getContacts().stream().filter(Filters.nameIsNotDX)
            .map(c -> new Contact(c, welsh)).collect(toList());
        this.courtTypes = courtEntity.getCourtTypes().stream().map(CourtType::getName).collect(toList());
        this.emails = courtEntity.getEmails().stream().map(e -> new Email(e, welsh)).collect(toList());
        this.openingTimes = courtEntity.getOpeningTimes().stream().map(OpeningTime::new).collect(toList());
        this.facilities = this.stripHtmlFromFacilities(
            courtEntity.getFacilities().stream().map(f -> new Facility(f, welsh)).collect(toList()));
        this.addresses = this.refactorAddressType(
            courtEntity.getAddresses().stream().map(a -> new CourtAddress(a, welsh)).collect(toList()));
        this.gbs = courtEntity.getGbs();
        this.dxNumbers = courtEntity.getContacts().stream().filter(Filters.nameIsDX).map(uk.gov.hmcts.dts.fact.entity.Contact::getNumber)
            .collect(toList());
        this.serviceArea = courtEntity.getServiceArea() == null ? null : courtEntity.getServiceArea().getService();
        this.inPerson = courtEntity.getInPerson() == null ? null : courtEntity.getInPerson().getIsInPerson();
    }

    private List<Facility> stripHtmlFromFacilities(List<Facility> facilities) {
        for (Facility facility : facilities) {
            facility.setDescription(Filters.stripHtmlFromString(facility.getDescription()));
        }
        return facilities;
    }

    private List<CourtAddress> refactorAddressType(List<CourtAddress> courtAddresses) {
        for (CourtAddress courtAddress : courtAddresses) {
            switch (courtAddress.getAddressType()) {
                case "Visit us or write to us":
                    courtAddress.setAddressType("Visit or contact us");
                    break;
                case "Postal":
                    courtAddress.setAddressType("Write to us");
                    break;
                case "Visiting":
                    courtAddress.setAddressType("Visit us");
                    break;
            }
        }
        return courtAddresses;
    }

}
