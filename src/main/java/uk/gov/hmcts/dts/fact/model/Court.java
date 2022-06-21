package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.dts.fact.entity.CourtAdditionalLink;
import uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate;
import uk.gov.hmcts.dts.fact.entity.CourtContact;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.util.AddressType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingInt;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
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
    "types", "emails", "contacts", "opening_times", "application_updates", "facilities", "addresses", "gbs", "dx_number",
    "service_area", "in_person", "access_scheme", "additional_links"})
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
    @JsonProperty("application_updates")
    private List<ApplicationUpdate> applicationUpdates;
    private List<Facility> facilities;
    private List<CourtAddress> addresses;
    private String gbs;
    @JsonProperty("dx_number")
    private List<String> dxNumbers;
    @JsonProperty("service_area")
    private List<String> serviceAreas;
    private Boolean inPerson;
    private Boolean accessScheme;
    private Boolean commonFlag;
    @JsonProperty("additional_links")
    private List<AdditionalLink> additionalLinks;
    @JsonProperty("service_centre")
    private ServiceCentre serviceCentre;

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
        this.areasOfLaw = getAreasOfLaw(courtEntity, courtEntity.isInPerson());
        this.contacts = getContacts(courtEntity);
        this.courtTypes = courtEntity.getCourtTypes().stream().map(CourtType::getName).collect(toList());
        this.emails = getEmails(courtEntity);
        this.openingTimes = getOpeningTimes(courtEntity);
        this.applicationUpdates = getApplicationUpdates(courtEntity);
        this.facilities = getFacilities(courtEntity);
        this.addresses = getCourtAddresses(courtEntity);
        this.gbs = courtEntity.getGbs();
        this.dxNumbers = getDxNumbers(courtEntity);
        this.serviceAreas = courtEntity.getServiceAreas() == null ? emptyList() : getServiceAreas(courtEntity);
        this.inPerson = courtEntity.isInPerson();
        this.accessScheme = courtEntity.getInPerson() == null ? null : courtEntity.getInPerson().getAccessScheme();
        this.additionalLinks = getAdditionalLink(courtEntity);
        this.serviceCentre = getServiceCentreDetails(courtEntity);
        this.commonFlag = courtEntity.getInPerson() == null ? null : courtEntity.getInPerson().getCommonPlatform();
    }

    private ServiceCentre getServiceCentreDetails(final uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        return new ServiceCentre(courtEntity.getServiceAreas().size() > 0,
                                 courtEntity.getServiceCentre() == null
                                     ? "" : courtEntity.getServiceCentre().getIntroParagraph(),
                                 courtEntity.getServiceCentre() == null
                                     ? "" : courtEntity.getServiceCentre().getIntroParagraphCy());
    }

    private List<CourtAddress> getCourtAddresses(final uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        // Return sorted court addresses with 'visit us' or 'visit or contact us' addresses appear first
        return courtEntity.getAddresses()
            .stream()
            .sorted(comparingInt(a -> AddressType.isCourtAddress(a.getAddressType().getName()) ? 0 : 1))
            .map(CourtAddress::new)
            .collect(toList());
    }

    private List<AreaOfLaw> getAreasOfLaw(final uk.gov.hmcts.dts.fact.entity.Court courtEntity, final boolean isInPerson) {
        return courtEntity.getAreasOfLaw()
            .stream()
            .map(areaOfLaw -> {
                AreaOfLaw areaOfLawObj = new AreaOfLaw(areaOfLaw, isInPerson);
                areaOfLawObj.setExternalLink(decodeUrlFromString(areaOfLawObj.getExternalLink()));
                return areaOfLawObj;
            })
            .collect(toList());
    }

    private List<Contact> getContacts(final uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        return ofNullable(courtEntity.getCourtContacts())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtContact::getContact)
            .map(Contact::new)
            .collect(toList());
    }

    private List<String> getDxNumbers(final uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        return ofNullable(courtEntity.getCourtDxCodes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(c -> c.getDxCode().getCode())
            .collect(toList());
    }

    private List<Email> getEmails(final uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        return ofNullable(courtEntity.getCourtEmails())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtEmail::getEmail)
            .map(Email::new)
            .collect(toList());
    }

    private List<OpeningTime> getOpeningTimes(final uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        return ofNullable(courtEntity.getCourtOpeningTimes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtOpeningTime::getOpeningTime)
            .map(OpeningTime::new)
            .collect(toList());
    }

    private List<ApplicationUpdate> getApplicationUpdates(final uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        return ofNullable(courtEntity.getCourtApplicationUpdates())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtApplicationUpdate::getApplicationUpdate)
            .map(ApplicationUpdate::new)
            .collect(toList());
    }

    private List<Facility> getFacilities(final uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        return ofNullable(courtEntity.getFacilities())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(facility -> {
                final Facility facilityObj = new Facility(facility);
                facilityObj.setDescription(stripHtmlFromString(facilityObj.getDescription()));
                return facilityObj;
            })
            .sorted(comparingInt(Facility::getOrder))
            .collect(toList());
    }

    private List<String> getServiceAreas(final uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        return courtEntity.getServiceAreas()
            .stream()
            .map(ServiceArea::getName)
            .collect(toList());
    }

    private List<AdditionalLink> getAdditionalLink(final uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        return ofNullable(courtEntity.getCourtAdditionalLinks())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtAdditionalLink::getAdditionalLink)
            .map(AdditionalLink::new)
            .collect(toList());
    }
}
