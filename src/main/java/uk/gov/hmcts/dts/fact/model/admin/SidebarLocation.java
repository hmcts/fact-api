package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SidebarLocation {
    private Integer id;
    private String name;

    public SidebarLocation(final uk.gov.hmcts.dts.fact.entity.SidebarLocation sidebarLocation) {
        this.id = sidebarLocation.getId();
        this.name = sidebarLocation.getName();
    }
}
