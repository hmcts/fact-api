package uk.gov.hmcts.dts.fact.mapit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import uk.gov.hmcts.dts.fact.services.MapitService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

public class MapitValidatorTest {

    private static final MapitService mockMapItService = Mockito.mock(MapitService.class);
    private final MapitValidator mapitValidator = new MapitValidator(mockMapItService);

    @BeforeAll
    public static void beforeAll() {
        Mockito.when(mockMapItService.getMapitData(Mockito.anyString()))
            .thenReturn(Optional.of(new MapitData()));
        when(mockMapItService.getMapitDataWithPartial(Mockito.anyString()))
            .thenReturn(Optional.of(new MapitData()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"M,,,P$11&PY,.@£*$&,)@*&!@£," +
        "ReEeEeeEaAaAllLlYLlLlLoOoOnNnG, MM"})
    public void testIfPostcodesAreInvalid(String arg) {
        String[] testPostcodesArray = arg.split("\\s*,\\s*");

        for (String testPostcode: testPostcodesArray) {
            assertFalse(mapitValidator.postcodeDataExists(testPostcode));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"M0, M00, M000, M0S, M05H, M0S5, M05H3, " +
        "MO5, MO53, MO533, MO5H, MO5H3, EC1W, W1J 7NT, DE12 8HJ, " +
        "SW1A 1AA, HD7 5UZ, CH5 3QW, SA63, W2 1JB, PL7 1RF, GIR 0AA, " +
        "JE3 1EP, JE2 3XP, IM9 4EB, IM9 4AJ, GY79YH"})
    public void testIfPostcodesAreValid(String arg) {
        String[] testPostcodesArray = arg.split("\\s*,\\s*");

        for (String testPostcode: testPostcodesArray) {
            assertTrue(mapitValidator.postcodeDataExists(testPostcode));
        }
    }
}
