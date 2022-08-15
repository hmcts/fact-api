package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CourtSecondaryAddressTypeTest {

    private static final List<String> AREAS_OF_LAW =
        Arrays.asList("area 1", "area 2", "area 3");
    private static final List<String> COURT_TYPES =
        Arrays.asList("court 1", "court 2", "court 3");

    @Test
    void testConstructor() {
        CourtSecondaryAddressType courtSecondaryAddressType
            = new CourtSecondaryAddressType(AREAS_OF_LAW, COURT_TYPES);
        assertEquals(courtSecondaryAddressType.getCourtTypesList(), COURT_TYPES);
        assertEquals(courtSecondaryAddressType.getAreaOfLawList(), AREAS_OF_LAW);
    }
}
