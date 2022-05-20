package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class County {
    private Integer id;
    private String name;
    private String country;

    public County(final uk.gov.hmcts.dts.fact.entity.County county) {
        this.id = county.getId();
        this.name = county.getName();
        this.country = county.getCountry();
    }
}
