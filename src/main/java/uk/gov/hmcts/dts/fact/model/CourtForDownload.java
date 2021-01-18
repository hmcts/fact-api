package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;
import uk.gov.hmcts.dts.fact.entity.CourtType;
import uk.gov.hmcts.dts.fact.entity.Facility;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
@SuppressWarnings("PMD.TooManyFields")
public class CourtForDownload {
    private static final String WWW_FIND_COURT_TRIBUNAL_SERVICE_GOV_UK_COURTS = "https://www.find-court-tribunal.service.gov.uk/courts/";
    private String name;
    private String open;
    private String updated;
    private String address;
    private String areasOfLaw;
    private String courtTypes;
    private Integer crownCourtCode;
    private Integer countyCourtCode;
    private Integer magistratesCourtCode;
    private String facilities;
    private String url;

    public CourtForDownload(uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        this.name = chooseString(courtEntity.getNameCy(), courtEntity.getName());
        this.open = courtEntity.getDisplayed() ? "open" : "closed";
        this.updated = courtEntity.getUpdatedAt() == null
            ? null : new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(courtEntity.getUpdatedAt());

        this.address = courtEntity
            .getAddresses()
            .stream().findFirst()
            .map(this::formatAddress)
            .orElse("");
        this.crownCourtCode = courtEntity.getNumber();
        this.countyCourtCode = courtEntity.getCciCode();
        this.magistratesCourtCode = courtEntity.getMagistrateCode();
        this.areasOfLaw = courtEntity.getAreasOfLaw().stream().map(AreaOfLaw::getName).collect(Collectors.joining(", "));

        this.courtTypes = courtEntity.getCourtTypes().stream().map(CourtType::getName).collect(Collectors.joining(", "));
        this.facilities = courtEntity
            .getFacilities()
            .stream()
            .map(Facility::getName)
            .collect(Collectors.joining(", "));
        this.url = WWW_FIND_COURT_TRIBUNAL_SERVICE_GOV_UK_COURTS + courtEntity.getSlug();
    }

    private String formatAddress(CourtAddress courtAddress) {
        return String.format(
            "%s%s,%s",
            courtAddress.getAddress()
                .replaceAll("\\n", ",")
                .replaceAll("\\r", ",")
                .replaceAll("\\t", ""),
            courtAddress.getTownName(),
            courtAddress.getPostcode()
        );
    }
}
