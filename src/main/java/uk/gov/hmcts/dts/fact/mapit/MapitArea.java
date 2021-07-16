package uk.gov.hmcts.dts.fact.mapit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapitArea {
    public String id;
    public String name;
    public String type;
}
