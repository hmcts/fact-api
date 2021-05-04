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
public class ContactType {
    private Integer id;
    private String type;
    @JsonProperty("type_cy")
    private String typeCy;

    public ContactType(final uk.gov.hmcts.dts.fact.entity.ContactType contactType) {
        this.id = contactType.getId();
        this.type = contactType.getName();
        this.typeCy = contactType.getNameCy();
    }
}
