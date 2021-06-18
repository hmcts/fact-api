package uk.gov.hmcts.dts.fact.services.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ValidationService.class)
public class ValidateServiceTest {

    @MockBean
    private PostcodeValidator postcodeValidator;

    @Autowired
    private ValidationService validationService;

    @Test
    public void testValidatePostcodesSuccess() {
        // Expect no strings to be returned if all checks have passed
        String[] testPostcodesArray = {"M0", "MO5", "MO53", "MO533"};
        when(postcodeValidator.postcodeDataExists(anyString())).thenReturn(true);
        assertThat(validationService.validatePostcodes(testPostcodesArray)).isEmpty();
    }

    @Test
    public void testValidatePostcodesInvalid() {
        // Expect an array of strings to be returned if one or more checks have failed
        // Note: this also tests that the stripping/trimming works, as else the mocking will not succeed
        when(postcodeValidator.postcodeDataExists("avalidpostcode")).thenReturn(true);
        when(postcodeValidator.postcodeDataExists("aninvalidpostcode")).thenReturn(false);
        when(postcodeValidator.postcodeDataExists("anotherinvalidpostcode")).thenReturn(false);

        String[] testPostcodesArray = {"a valid postcode", "an invalid postcode", "another invalid postcode"};
        assertThat(validationService.validatePostcodes(testPostcodesArray)).containsExactly(
            testPostcodesArray[1],
            testPostcodesArray[2]
        );

        verify(postcodeValidator).postcodeDataExists("avalidpostcode");
        verify(postcodeValidator).postcodeDataExists("aninvalidpostcode");
        verify(postcodeValidator).postcodeDataExists("anotherinvalidpostcode");
    }
}
