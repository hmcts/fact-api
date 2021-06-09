package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalAuthority {

    private Integer id;
    private String name;

    public LocalAuthority(uk.gov.hmcts.dts.fact.entity.LocalAuthority localAuthority) {
        this.id = localAuthority.getId();
        this.name = localAuthority.getName();

    }


}
