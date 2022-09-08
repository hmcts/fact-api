package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Region {
    private Integer id;
    private String name;
    private String country;

    public Region(final uk.gov.hmcts.dts.fact.entity.Region region) {
        this.id = region.getId();
        this.name = region.getName();
        this.country = region.getCountry();
    }
}
