package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Getter
@NoArgsConstructor
@JsonPropertyOrder({"name", "slug", "distance"})
public class CourtReferenceWithDistance {
    private String name;
    private String slug;
    private BigDecimal distance;

    public CourtReferenceWithDistance(final uk.gov.hmcts.dts.fact.entity.CourtWithDistance courtEntity) {
        this.name = chooseString(courtEntity.getNameCy(), courtEntity.getName());
        this.slug = courtEntity.getSlug();
        this.distance = BigDecimal.valueOf(courtEntity.getDistance()).setScale(2, RoundingMode.HALF_UP);
    }
}
