package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Getter
@NoArgsConstructor
@JsonPropertyOrder({"name", "slug", "open", "distance", "areasOfLawSpoe"})
public class CourtReferenceWithDistance {
    private String name;
    private String slug;
    private Boolean open;
    private BigDecimal distance;
    private List<String> areasOfLawSpoe;

    public CourtReferenceWithDistance(final uk.gov.hmcts.dts.fact.entity.CourtWithDistance courtEntity) {
        this.name = chooseString(courtEntity.getNameCy(), courtEntity.getName());
        this.slug = courtEntity.getSlug();
        this.open = courtEntity.getDisplayed();
        this.areasOfLawSpoe = courtEntity.getAreasOfLawSpoe();

        ofNullable(courtEntity.getDistance())
            .ifPresent(value -> this.distance = BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP));
    }
}
