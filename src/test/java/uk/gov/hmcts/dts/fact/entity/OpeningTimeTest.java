package uk.gov.hmcts.dts.fact.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OpeningTimeTest {
    private static final String DESCRIPTION = "description";
    private static final String DESCRIPTION_CY = "description cy";
    private static final String HOURS = "9 to 5";

    @Test
    void testOpeningTimeConstructor() {
        OpeningTime openingTime = new OpeningTime(DESCRIPTION, DESCRIPTION_CY, HOURS);
        assertThat(openingTime.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(openingTime.getDescriptionCy()).isEqualTo(DESCRIPTION_CY);
        assertThat(openingTime.getHours()).isEqualTo(HOURS);
        assertThat(openingTime.getAdminType()).isEqualTo(null);
    }
}
