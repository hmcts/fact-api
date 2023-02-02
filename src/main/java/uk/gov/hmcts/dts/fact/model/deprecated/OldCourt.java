package uk.gov.hmcts.dts.fact.model.deprecated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.dts.fact.entity.*;
import uk.gov.hmcts.dts.fact.model.Contact;
import uk.gov.hmcts.dts.fact.model.Email;
import uk.gov.hmcts.dts.fact.model.OpeningTime;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuppressWarnings({"PMD.TooManyFields", "PMD.UnusedAssignment"})
@JsonPropertyOrder({"name", "slug", "info", "open", "directions", "lat", "lon",
    "crown_location_code", "county_location_code", "cci_code", "magistrates_location_code", "areas_of_law",
    "types", "emails", "contacts", "opening_times", "facilities", "addresses", "gbs"})
public class OldCourt {
    private String name;
    private String slug;
    private String info;
    private Boolean open;
    private String directions;
    private Double lat;
    private Double lon;
    private Integer crownLocationCode;
    private Integer countyLocationCode;
    private Integer cciCode;
    private Integer locationCode;
    private Integer courtCode;
    private Integer magistratesLocationCode;
    private List<String> areasOfLaw;
    @JsonProperty("areas_of_law_spoe")
    private List<String> areasOfLawSpoe;
    @JsonProperty("types")
    private List<String> courtTypes;
    private List<Email> emails;
    private List<Contact> contacts;
    private List<OpeningTime> openingTimes;
    private List<OldFacility> facilities;
    private List<OldCourtAddress> addresses;
    private String gbs;

    public OldCourt(final Court courtEntity) {
        this.name = chooseString(courtEntity.getNameCy(), courtEntity.getName());
        this.slug = courtEntity.getSlug();
        this.info = chooseString(courtEntity.getInfoCy(), courtEntity.getInfo());
        this.open = courtEntity.getDisplayed();
        this.directions = chooseString(courtEntity.getDirectionsCy(), courtEntity.getDirections());
        this.lat = courtEntity.getLat();
        this.lon = courtEntity.getLon();
        this.crownLocationCode = courtEntity.getNumber();
        this.countyLocationCode = courtEntity.getCciCode();
        this.cciCode = courtEntity.getCciCode();
        this.locationCode = courtEntity.getLocationCode();
        this.courtCode = courtEntity.getCourtCode();
        this.magistratesLocationCode = courtEntity.getMagistrateCode();
        this.areasOfLaw = courtEntity.getAreasOfLaw().stream().map(AreaOfLaw::getName)
            .collect(toList());
        this.courtTypes = ofNullable(courtEntity.getCourtTypes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtType::getName)
            .collect(toList());
        this.areasOfLawSpoe = courtEntity.getAreasOfLawSpoe();
        this.courtTypes = courtEntity.getCourtTypes().stream().map(CourtType::getName).collect(toList());
        this.emails = ofNullable(courtEntity.getCourtEmails())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtEmail::getEmail)
            .map(Email::new).collect(toList());
        this.contacts = getContactsWithDx(courtEntity);
        this.openingTimes = ofNullable(courtEntity.getCourtOpeningTimes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtOpeningTime::getOpeningTime)
            .map(OpeningTime::new).collect(toList());
        this.facilities = ofNullable(courtEntity.getFacilities())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(OldFacility::new)
            .collect(toList());
        this.addresses = ofNullable(courtEntity.getAddresses())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(OldCourtAddress::new)
            .collect(toList());
        this.gbs = courtEntity.getGbs();
    }

    private List<Contact> getContactsWithDx(final Court courtEntity) {
        List<Contact> dxContacts = ofNullable(courtEntity.getCourtContacts())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtContact::getContact)
            .map(Contact::new)
            .collect(toList());

        List<DxCode> dxCodes = ofNullable(courtEntity.getCourtDxCodes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtDxCode::getDxCode)
            .collect(toList());

        dxCodes.forEach(dx -> {
            final uk.gov.hmcts.dts.fact.entity.Contact contactEntity = new uk.gov.hmcts.dts.fact.entity.Contact();
            contactEntity.setDescription("DX");
            contactEntity.setExplanation(dx.getExplanation());
            contactEntity.setExplanationCy(dx.getExplanationCy());
            contactEntity.setNumber(dx.getCode());
            dxContacts.add(new Contact(contactEntity));
        });

        return dxContacts;
    }
}
