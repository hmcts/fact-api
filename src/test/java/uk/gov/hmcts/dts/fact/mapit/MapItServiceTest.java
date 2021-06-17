package uk.gov.hmcts.dts.fact.mapit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class MapItServiceTest {

    private final MapitValidator mockMapitValidator = Mockito.mock(MapitValidator.class);
    private final MapItService mapItService = new MapItService(mockMapitValidator);

    @Test
    public void testValidatePostcodesSuccess() {
        // Expect no strings to be returned if all checks have passed
        String[] testPostcodesArray = {"M0", "MO5", "MO53", "MO533"};
        Mockito.when(mockMapitValidator.postcodeDataExists(anyString())).thenReturn(true);
        assertEquals(0, mapItService.validatePostcodes(testPostcodesArray).length);
    }

    @Test
    public void testValidatePostcodesInvalid() {
        // Expect an array of strings to be returned if one or more checks have failed
        // Note: this also tests that the stripping/trimming works, as else the mocking will not succeed
        Mockito.when(mockMapitValidator.postcodeDataExists("avalidpostcode")).thenReturn(true);
        Mockito.when(mockMapitValidator.postcodeDataExists("aninvalidpostcode")).thenReturn(false);
        Mockito.when(mockMapitValidator.postcodeDataExists("anotherinvalidpostcode")).thenReturn(false);

        String[] testPostcodesArray = {"a valid postcode", "an invalid postcode", "another invalid postcode"};
        assertArrayEquals(new String[]{testPostcodesArray[1], testPostcodesArray[2]},
                          mapItService.validatePostcodes(testPostcodesArray));

        Mockito.verify(mockMapitValidator, Mockito.times(1))
            .postcodeDataExists("avalidpostcode");
        Mockito.verify(mockMapitValidator, Mockito.times(1))
            .postcodeDataExists("aninvalidpostcode");
        Mockito.verify(mockMapitValidator, Mockito.times(1))
            .postcodeDataExists("anotherinvalidpostcode");
    }
}
