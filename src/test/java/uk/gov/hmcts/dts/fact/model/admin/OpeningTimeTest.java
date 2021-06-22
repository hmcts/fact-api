package uk.gov.hmcts.dts.fact.model.admin;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.OpeningType;

import static org.assertj.core.api.Assertions.assertThat;

public class OpeningTimeTest {
    private static final String DESCRIPTION = "description";
    private static final String DESCRIPTION_CY = "description cy";
    private static final String HOURS = "9 to 5";
    private static final int ID = 10;

    @Test
    void testOpeningTimeConstructionWithAdminType() {
        OpeningType openingType = new OpeningType(ID, DESCRIPTION, DESCRIPTION_CY);
        uk.gov.hmcts.dts.fact.entity.OpeningTime openingTimeEntity = new uk.gov.hmcts.dts.fact.entity.OpeningTime(openingType, HOURS);
        OpeningTime openingTime = new OpeningTime(openingTimeEntity);
        assertThat(openingTime.getHours()).isEqualTo(HOURS);
        assertThat(openingTime.getTypeId()).isNotNull();
    }

    @Test
    void testOpeningTimeConstructionWithoutAdminType() {
        uk.gov.hmcts.dts.fact.entity.OpeningTime openingTimeEntity = new uk.gov.hmcts.dts.fact.entity.OpeningTime(DESCRIPTION, DESCRIPTION_CY, HOURS);

        OpeningTime openingTime = new OpeningTime(openingTimeEntity);
        assertThat(openingTime.getHours()).isEqualTo(HOURS);
        assertThat(openingTime.getTypeId()).isNull();
    }
}
