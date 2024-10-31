package uk.gov.hmcts.dts.fact.services.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.exception.InvalidEpimIdException;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.model.admin.CourtAddress;
import uk.gov.hmcts.dts.fact.model.admin.CourtSecondaryAddressType;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ValidationService.class)
class ValidateServiceTest {

    @MockBean
    private PostcodeValidator postcodeValidator;

    @MockBean
    private LocalAuthorityValidator localAuthorityValidator;

    @Autowired
    private ValidationService validationService;

    private static final List<String> TEST_ADDRESS_LINES = singletonList("1 High Street");
    private static final String TEST_TOWN = "London";
    private static final String TEST_POSTCODE = "NW1 8AF";
    private static final List<AreaOfLaw> COURT_SECONDARY_ADDRESS_AREAS_OF_LAW = asList(
        new AreaOfLaw(
            new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(
                34_257, "Civil partnership"), false),
        new AreaOfLaw(new uk.gov.hmcts.dts.fact.entity.AreaOfLaw(
            34_248, "Adoption"), false)
    );
    private static final List<CourtType> COURT_SECONDARY_ADDRESS_COURT_TYPES = asList(
        new CourtType(
            new uk.gov.hmcts.dts.fact.entity.CourtType(11_417, "Family Court", "Family")
        ),
        new CourtType(
            new uk.gov.hmcts.dts.fact.entity.CourtType(11_418, "Tribunal","Search")
        )
    );
    private static final CourtSecondaryAddressType TEST_SECONDARY_ADDRESS_TYPE_LIST = new CourtSecondaryAddressType(
        COURT_SECONDARY_ADDRESS_AREAS_OF_LAW,
        COURT_SECONDARY_ADDRESS_COURT_TYPES
    );
    private static final String BAD_EPIM_ID = "BAD EPIM!";
    private static final String GOOD_EPIM_ID = "GOOD-EPIM";
    private static final CourtAddress BAD_EPIM_ADDRESS = new CourtAddress(
        1,
        5581,
        TEST_ADDRESS_LINES,
        TEST_ADDRESS_LINES,
        TEST_TOWN,
        TEST_TOWN,
        1,
        TEST_POSTCODE,
        TEST_SECONDARY_ADDRESS_TYPE_LIST,
        1,
        BAD_EPIM_ID
    );
    private static final CourtAddress GOOD_EPIM_ADDRESS = new CourtAddress(
        1,
        5581,
        TEST_ADDRESS_LINES,
        TEST_ADDRESS_LINES,
        TEST_TOWN,
        TEST_TOWN,
        1,
        TEST_POSTCODE,
        TEST_SECONDARY_ADDRESS_TYPE_LIST,
        1,
        GOOD_EPIM_ID
    );

    @Test
    void testValidatePostcodesSuccess() {
        // Expect no strings to be returned if all checks have passed
        final List<String> testPostcodes = Arrays.asList("M0", "MO5", "MO53", "MO533");
        when(postcodeValidator.postcodeDataExists(anyString())).thenReturn(true);
        assertThat(validationService.validatePostcodes(testPostcodes)).isEmpty();
    }

    @Test
    void testValidatePostcodesInvalid() {
        // Expect an array of strings to be returned if one or more checks have failed
        // Note: this also tests that the stripping/trimming works, as else the mocking will not succeed
        when(postcodeValidator.postcodeDataExists("avalidpostcode")).thenReturn(true);
        when(postcodeValidator.postcodeDataExists("aninvalidpostcode")).thenReturn(false);
        when(postcodeValidator.postcodeDataExists("anotherinvalidpostcode")).thenReturn(false);

        final List<String> testPostcodes = Arrays.asList("a valid postcode", "an invalid postcode", "another invalid postcode");
        assertThat(validationService.validatePostcodes(testPostcodes))
            .containsExactly("an invalid postcode", "another invalid postcode");

        verify(postcodeValidator).postcodeDataExists("avalidpostcode");
        verify(postcodeValidator).postcodeDataExists("aninvalidpostcode");
        verify(postcodeValidator).postcodeDataExists("anotherinvalidpostcode");
    }

    @Test
    void testValidateFullPostcodesSuccess() {
        final List<String> testPostcodes = Arrays.asList("SW1A 1AA", "M11AA");
        when(postcodeValidator.fullPostcodeValid(anyString())).thenReturn(true);
        assertThat(validationService.validateFullPostcodes(testPostcodes)).isEmpty();
    }

    @Test
    void testValidateFullPostcodesInvalid() {
        final String testPostcode1 = "SW1A 1AA";
        final String testPostcode1WithoutSpace = "SW1A1AA";
        final String testPostcode2 = "M11";
        final String testPostcode3 = "M11AA";
        final List<String> testPostcodes = Arrays.asList(testPostcode1, testPostcode2, testPostcode3);

        when(postcodeValidator.fullPostcodeValid(testPostcode1WithoutSpace)).thenReturn(true);
        when(postcodeValidator.fullPostcodeValid(testPostcode2)).thenReturn(false);
        when(postcodeValidator.fullPostcodeValid(testPostcode3)).thenReturn(true);

        assertThat(validationService.validateFullPostcodes(testPostcodes)).containsExactly(testPostcode2);
    }

    @Test
    void testValidateWhenLocalAuthorityIsValid() {
        when(localAuthorityValidator.localAuthorityNameIsValid(any())).thenReturn(true);
        assertThat(validationService.validateLocalAuthority("Birmingham City Council")).isTrue();
    }

    @Test
    void testValidateWhenLocalAuthorityIsInvalid() {
        when(localAuthorityValidator.localAuthorityNameIsValid(any())).thenReturn(false);
        assertThat(validationService.validateLocalAuthority("Brmgham Council")).isFalse();
    }

    @Test
    void testValidateEpimIdsIsValid() {
        assertDoesNotThrow(() ->
            validationService.validateEpimIds(Collections.singletonList(GOOD_EPIM_ADDRESS))
        );
    }

    @Test
    void testValidateEpimIdsIsInvalid() {
        assertThrows(InvalidEpimIdException.class, () ->
            validationService.validateEpimIds(Collections.singletonList(BAD_EPIM_ADDRESS))
        );
    }

}
