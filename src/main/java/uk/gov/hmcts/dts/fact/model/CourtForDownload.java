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
import java.util.Locale;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

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
            .map(this::formatEmail)
            .collect(joining(lineSeparator()));
        this.contacts = ofNullable(courtEntity.getContacts())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(this::formatContact)
            .collect(joining(lineSeparator()));
        this.openingTimes = ofNullable(courtEntity.getCourtOpeningTimes())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CourtOpeningTime::getOpeningTime)
            .map(this::formatOpeningTime)
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

    private String formatEmail(uk.gov.hmcts.dts.fact.entity.Email email) {
        StringBuffer formatted = new StringBuffer(format(
            "Address: %s, Description: %s",
            email.getAddress(),
            email.getDescription()
        ));
        if (email.getExplanation() != null && !email.getExplanation().isBlank()) {
            formatted.append(format(", Explanation: %s", email.getExplanation()));
        }
        return formatted.toString();
    }

    private String formatContact(uk.gov.hmcts.dts.fact.entity.Contact contact) {
        StringBuffer formatted = new StringBuffer(format(
            "Number: %s, Description: %s",
            contact.getNumber(),
            contact.getName()
        ));
        if (contact.getExplanation() != null && !contact.getExplanation().isBlank()) {
            formatted.append(format(", Explanation: %s", contact.getExplanation()));
        }
        return formatted.toString();
    }

    private String formatOpeningTime(uk.gov.hmcts.dts.fact.entity.OpeningTime openingTime) {
        return format("Description: %s, Hours: %s", openingTime.getType(), openingTime.getHours());
    }
}
