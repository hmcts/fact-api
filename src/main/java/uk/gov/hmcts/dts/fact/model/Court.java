package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.dts.fact.entity.CourtType;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.Utils.*;

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

    public Court(uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        this.name = chooseString(courtEntity.getNameCy(), courtEntity.getName());
        this.slug = courtEntity.getSlug();
        this.info = stripHtmlFromString(chooseString(courtEntity.getInfoCy(), courtEntity.getInfo()));
        this.open = courtEntity.getDisplayed();
        this.directions = chooseString(courtEntity.getDirectionsCy(), courtEntity.getDirections());
        this.imageFile = courtEntity.getImageFile();
        this.lat = courtEntity.getLat();
        this.lon = courtEntity.getLon();
        this.alert = chooseString(courtEntity.getAlertCy(), courtEntity.getAlert());
        this.crownLocationCode = courtEntity.getNumber();
        this.countyLocationCode = courtEntity.getCciCode();
        this.magistratesLocationCode = courtEntity.getMagistrateCode();
        this.areasOfLaw = this.stripUrlencodedFromAreaOfLaw(
            courtEntity.getAreasOfLaw().stream().map(AreaOfLaw::new).collect(toList()));
        this.contacts = courtEntity.getContacts().stream().filter(nameIsNotDX)
            .map(Contact::new).collect(toList());
        this.courtTypes = courtEntity.getCourtTypes().stream().map(CourtType::getName).collect(toList());
        this.emails = courtEntity.getEmails().stream().map(Email::new).collect(toList());
        this.openingTimes = courtEntity.getOpeningTimes().stream().map(OpeningTime::new).collect(toList());
        this.facilities = this.stripHtmlFromFacilities(
            courtEntity.getFacilities().stream().map(Facility::new).collect(toList()));
        this.addresses = this.refactorAddressType(
            courtEntity.getAddresses().stream().map(CourtAddress::new).collect(toList()));
        this.gbs = courtEntity.getGbs();
        this.dxNumbers = courtEntity.getContacts().stream().filter(nameIsDX).map(uk.gov.hmcts.dts.fact.entity.Contact::getNumber)
            .collect(toList());
        this.serviceArea = courtEntity.getServiceArea() == null ? null : courtEntity.getServiceArea().getService();
        this.inPerson = courtEntity.getInPerson() == null ? null : courtEntity.getInPerson().getIsInPerson();
    }

    private List<Facility> stripHtmlFromFacilities(List<Facility> facilities) {
        for (Facility facility : facilities) {
            facility.setDescription(stripHtmlFromString(facility.getDescription()));
        }
        return facilities;
    }

    private List<AreaOfLaw> stripUrlencodedFromAreaOfLaw(List<AreaOfLaw> areaOfLaws) {
        for (AreaOfLaw areaOfLaw : areaOfLaws) {
            areaOfLaw.setExternalLink(stripUrlencodedFromString(areaOfLaw.getExternalLink()));
        }
        return areaOfLaws;
    }

    private List<CourtAddress> refactorAddressType(List<CourtAddress> courtAddresses) {
        for (CourtAddress courtAddress : courtAddresses) {
            if (courtAddress.getAddressType().equals("Visit us or write to us")) {
                courtAddress.setAddressType("Visit or contact us");
            } else if (courtAddress.getAddressType().equals("Postal")) {
                courtAddress.setAddressType("Write to us");
            } else if (courtAddress.getAddressType().equals("Visiting")) {
                courtAddress.setAddressType("Visit us");
            }
        }
        return courtAddresses;
    }

}
