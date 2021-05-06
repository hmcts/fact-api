package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmailType {

    private Integer id;
    private String description;
    @JsonProperty("description_cy")
    private String descriptionCy;

    public EmailType(final uk.gov.hmcts.dts.fact.entity.EmailType emailType) {
        this.id = emailType.getId();
        this.description = emailType.getDescription();
        this.descriptionCy = emailType.getDescriptionCy();
    }
}
