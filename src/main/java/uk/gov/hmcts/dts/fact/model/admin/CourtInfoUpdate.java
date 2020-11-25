package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CourtInfoUpdate {
    private final List<String> courts;
    private String info;
    @JsonProperty("info_cy")
    private final String infoCy;
}
