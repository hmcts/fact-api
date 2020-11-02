package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"name", "name_cy", "info", "info_cy", "open", "urgent_message", "urgent_message_cy"})
public class CourtGeneral {
    private String name;
    @JsonProperty("name_cy")
    private String nameCy;
    private String info;
    @JsonProperty("info_cy")
    private String infoCy;
    private Boolean open;
    @JsonProperty("urgent_message")
    private String alert;
    @JsonProperty("urgent_message_cy")
    private String alertCy;
}
