package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;

@Getter
@NoArgsConstructor
@JsonPropertyOrder({"name", "slug", "updated_at"})
public class CourtReference {
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM YYYY");

    private String name;
    private String slug;
    @JsonProperty("updated_at")
    private String updatedAt;

    public CourtReference(uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        this.name = courtEntity.getName();
        this.slug = courtEntity.getSlug();
        this.updatedAt = courtEntity.getUpdatedAt() == null ? null : dateFormat.format(courtEntity.getUpdatedAt());
    }
}
