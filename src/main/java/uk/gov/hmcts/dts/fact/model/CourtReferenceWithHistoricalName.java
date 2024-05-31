package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.CourtHistory;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;
@Getter
@NoArgsConstructor
@JsonPropertyOrder({"name", "slug", "updated_at"})
public class CourtReferenceWithHistoricalName {

    private String name;
    private String slug;
    @JsonProperty("updated_at")
    private String updatedAt;
    private boolean displayed;
    private Integer region;
    private String historicalName;

    public CourtReferenceWithHistoricalName(uk.gov.hmcts.dts.fact.entity.Court courtEntity, CourtHistory courtHistory) {
        this.name = chooseString(courtEntity.getNameCy(), courtEntity.getName());
        this.slug = courtEntity.getSlug();
        this.updatedAt = courtEntity.getUpdatedAt() == null
            ? null : new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(courtEntity.getUpdatedAt());
        this.displayed = courtEntity.getDisplayed();
        this.region = courtEntity.getRegionId();
        this.historicalName = chooseString(courtHistory.getCourtNameCy(), courtHistory.getCourtName());
    }
}
