package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate;
import uk.gov.hmcts.dts.fact.entity.EmailType;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ApplicationUpdate {
    private String type;
    private String email;
    @JsonProperty("external_link")
    private String externalLink;
    @JsonProperty("external_link_description")
    private String externalLinkDescription;
    private int adminApplicationUpdateTypeId;

    public ApplicationUpdate(uk.gov.hmcts.dts.fact.entity.ApplicationUpdate applicationUpdate) {
        this.type = chooseString(applicationUpdate.getTypeCy(), applicationUpdate.getType());
        this.email = applicationUpdate.getEmail();
        this.externalLink = applicationUpdate.getExternalLink();
        this.externalLinkDescription = chooseString(applicationUpdate.getExternalLinkDescriptionCy(),
                                                    applicationUpdate.getExternalLinkDescription());
        /*
        if (applicationUpdate.getUpdateType() != null){
            this.adminApplicationUpdateTypeId = applicationUpdate.getUpdateType().getId();
        }

         */
    }

}
