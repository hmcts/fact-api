package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCentre {

    @JsonProperty("is_a_service_centre")
    private boolean isAServiceCentre;
    @JsonProperty("intro_paragraph")
    private String introParagraph;
    @JsonProperty("intro_paragraph_cy")
    private String introParagraphCy;
}
