package uk.gov.hmcts.dts.fact.model.admin;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NewCourt {
    @Size(min = 1, max = 200)
    @Pattern(regexp = "^[a-zA-Z0-9-'() ]*$",
        message = "Court name is not valid, should only contain a combination of characters, "
            + "numbers, apostrophes or hyphens")
    private String newCourtName;
    private Boolean serviceCentre;
    private List<String> serviceAreas;
    private double lon;
    private double lat;
}
