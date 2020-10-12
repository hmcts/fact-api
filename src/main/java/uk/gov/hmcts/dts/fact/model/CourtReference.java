package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonPropertyOrder({"name", "slug"})
public class CourtReference {
    private String name;
    private String slug;

    public CourtReference(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
