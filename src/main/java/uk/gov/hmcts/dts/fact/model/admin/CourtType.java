package uk.gov.hmcts.dts.fact.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CourtType {

    private Integer id;
    private String name;
    private String search;
    private Integer code;

    public CourtType(uk.gov.hmcts.dts.fact.entity.CourtType courtType) {
        this.id = courtType.getId();
        this.name = courtType.getName();
        this.search = courtType.getSearch();
    }

}
