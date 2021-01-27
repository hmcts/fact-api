package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.Facility;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
@SuppressWarnings("PMD.TooManyFields")
public class CourtForDownload {
    private String name;
    private String open;
    private String updated;
    private List<CourtAddress> addresses;
    private String areasOfLaw;
    private String courtTypes;
    private Integer crownCourtCode;
    private Integer countyCourtCode;
    private Integer magistratesCourtCode;
    private String facilities;
    private String slug;
    private List<Email> emails;
    private List<Contact> contacts;
    private List<OpeningTime> openingTimes;

    public CourtForDownload(uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        this.name = courtEntity.getName();
        this.open = courtEntity.getDisplayed() ? "open" : "closed";
        this.updated = courtEntity.getUpdatedAt() == null
            ? null : new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(courtEntity.getUpdatedAt());

        this.addresses = ofNullable(courtEntity.getAddresses())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtAddress::new)
            .collect(toList());

        this.crownCourtCode = courtEntity.getNumber();
        this.countyCourtCode = courtEntity.getCciCode();
        this.magistratesCourtCode = courtEntity.getMagistrateCode();
        this.areasOfLaw = ofNullable(courtEntity.getAreasOfLaw())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(AreaOfLaw::getName)
            .collect(joining(", "));
        this.courtTypes = ofNullable(courtEntity.getCourtTypes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtType::getName)
            .collect(joining(", "));
        this.facilities = ofNullable(courtEntity.getFacilities())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(Facility::getName)
            .collect(joining(", "));
        this.slug = courtEntity.getSlug();
        this.emails = ofNullable(courtEntity.getCourtEmails())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtEmail::getEmail)
            .map(Email::new)
            .collect(toList());
        this.contacts = ofNullable(courtEntity.getContacts())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(Contact::new)
            .collect(toList());
        this.openingTimes = ofNullable(courtEntity.getCourtOpeningTimes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtOpeningTime::getOpeningTime)
            .map(OpeningTime::new).collect(toList());
    }
}
