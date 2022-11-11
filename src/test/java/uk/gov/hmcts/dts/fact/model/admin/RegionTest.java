package uk.gov.hmcts.dts.fact.model.admin;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.Region;

import static org.assertj.core.api.Assertions.assertThat;

public class RegionTest {
    private static final Integer TEST_ID = 9;
    private static final String TEST_NAME = "North West";
    private static final String TEST_COUNTRY = "England";
    private static final Region REGION = new Region();

    @BeforeAll
    static void setUp() {
        REGION.setId(TEST_ID);
        REGION.setName(TEST_NAME);
        REGION.setCountry(TEST_COUNTRY);
    }

    @Test
    void testCreationOfRegion() {
        uk.gov.hmcts.dts.fact.model.admin.Region regionModel = new uk.gov.hmcts.dts.fact.model.admin.Region(REGION);

        assertThat(regionModel.getId()).isEqualTo(TEST_ID);
        assertThat(regionModel.getName()).isEqualTo(TEST_NAME);
        assertThat(regionModel.getCountry()).isEqualTo(TEST_COUNTRY);
    }
}
