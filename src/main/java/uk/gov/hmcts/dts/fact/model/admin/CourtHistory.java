package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourtHistory {
    private Integer id;

    @JsonProperty("search_court_id")
    private Integer searchCourtId;

    @JsonProperty("court_name")
    private String courtName;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public CourtHistory(uk.gov.hmcts.dts.fact.entity.CourtHistory courtHistory) {
        this.id = courtHistory.getId();
        this.searchCourtId = courtHistory.getSearchCourtId();
        this.courtName = courtHistory.getCourtName();
        this.updatedAt = courtHistory.getUpdatedAt();
        this.createdAt = courtHistory.getCreatedAt();
    }

}
