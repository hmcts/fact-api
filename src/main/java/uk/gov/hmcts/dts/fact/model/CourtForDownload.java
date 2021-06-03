package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtContact;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.entity.CourtOpeningTime;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.Facility;
import uk.gov.hmcts.dts.fact.entity.util.ElementFormatter;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.Utils.NAME_IS_DX;
import static uk.gov.hmcts.dts.fact.util.Utils.NAME_IS_NOT_DX;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
@SuppressWarnings({"PMD.TooManyFields", "PMD.UnnecessaryFullyQualifiedName"})
public class CourtForDownload {
    private static final String DX = "DX";
    private String name;
    private String open;
    private String updated;
    private String addresses;
    private String areasOfLaw;
    private String courtTypes;
    private Integer crownCourtCode;
    private Integer countyCourtCode;
    private Integer magistratesCourtCode;
    private String facilities;
    private String slug;
    private String emails;
    private String contacts;
    private String openingTimes;
    private String dxNumber;

    public CourtForDownload(uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        this.name = courtEntity.getName();
        this.open = courtEntity.getDisplayed() ? "open" : "closed";
        this.updated = courtEntity.getUpdatedAt() == null
            ? null : new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(courtEntity.getUpdatedAt());
        this.addresses = ofNullable(courtEntity.getAddresses())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(this::formatAddress)
            .collect(joining(lineSeparator()));
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
            .map(ElementFormatter::formatEmail)
            .collect(joining(lineSeparator()));
        final List<uk.gov.hmcts.dts.fact.entity.Contact> contacts = ofNullable(courtEntity.getCourtContacts())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtContact::getContact)
            .collect(toList());
        this.contacts = contacts.stream()
            .filter(NAME_IS_NOT_DX)
            .map(ElementFormatter::formatContact)
            .collect(joining(lineSeparator()));
        this.openingTimes = ofNullable(courtEntity.getCourtOpeningTimes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtOpeningTime::getOpeningTime)
            .map(ElementFormatter::formatOpeningTime)
            .collect(joining(lineSeparator()));
        this.dxNumber = contacts.stream()
            .filter(NAME_IS_DX)
            .map(ElementFormatter::formatContact)
            .collect(joining(lineSeparator()));
    }

    private String formatAddress(uk.gov.hmcts.dts.fact.entity.CourtAddress courtAddress) {
        return format(
            "%s: %s, %s, %s",
            courtAddress.getAddressType().getName(),
            courtAddress.getAddress()
                .lines()
                .filter(s -> !s.isBlank())
                .map(s -> s.replaceAll("\t", ""))
                .collect(joining(", ")),
            courtAddress.getTownName(),
            courtAddress.getPostcode()
        );
    }
}
