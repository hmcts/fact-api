package uk.gov.hmcts.dts.fact.migration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@SuppressWarnings("ClassCanBeRecord") // Keep Lombok POJO until Checkstyle handles records
public class CourtLocalAuthorityData {
    private final Integer id;
    @JsonProperty("area_of_law_id")
    private final Integer areaOfLawId;
    @JsonProperty("local_authority_ids")
    private final List<Integer> localAuthorityIds;
}
