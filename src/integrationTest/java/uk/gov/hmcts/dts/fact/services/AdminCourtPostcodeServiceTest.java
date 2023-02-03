package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtPostcodeService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest()
class AdminCourtPostcodeServiceTest {
    private static final String TEST_MANCHESTER_COURT_SLUG = "manchester-civil-justice-centre-civil-and-family-courts";
    private static final String EXISTED_POSTCODE = "M21";
    private static final String EXISTED_POSTCODE_LOWER_CASE = "m21";
    private static final String EXISTED_POSTCODE_WITH_SPACE = "M2 1";
    private static final List<String> TEST_POSTCODES = Arrays.asList(
        EXISTED_POSTCODE,
        EXISTED_POSTCODE_LOWER_CASE,
        EXISTED_POSTCODE_WITH_SPACE
    );

    @Autowired
    AdminCourtPostcodeService adminService;

    @Test
    void testCheckPostcodesExistIgnoresCasingAndSpaces() {
        assertThatCode(() -> adminService.checkPostcodesExist(TEST_MANCHESTER_COURT_SLUG, TEST_POSTCODES))
            .doesNotThrowAnyException();
    }
}
