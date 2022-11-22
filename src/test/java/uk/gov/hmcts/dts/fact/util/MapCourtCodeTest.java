package uk.gov.hmcts.dts.fact.util;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MapCourtCodeTest {

    private static final String UNKNOWN_COURT_TYPE_PREFIX = "Unknown court type: ";
    private static final String UNKNOWN_COURT_TYPE = "Any type";

    private static final List<CourtType> COURT_TYPES = Arrays.asList(
        new CourtType(1, "Magistrates' Court", 100),
        new CourtType(2,"County Court",101),
        new CourtType(3, "Crown Court",102)
    );

    private static final List<CourtType>  INCORRECT_COURT_TYPES = Collections.singletonList(
        new CourtType(1, UNKNOWN_COURT_TYPE, 100)
    );

    private final MapCourtCode mapCourtCode = new MapCourtCode();
    private Court court;

    @BeforeEach
    void initialise() {
        court = new Court();
    }

    @Test
    void shouldReturnMapCourtCodesForCourtEntity() {
        assertEquals(court, mapCourtCode.mapCourtCodesForCourtEntity(COURT_TYPES, court));
    }

    @Test
    void shouldMapCourtCodesForCourtTypeModel() {
        final Court courtReturn = mapCourtCode.mapCourtCodesForCourtEntity(COURT_TYPES, court);
        assertEquals(COURT_TYPES, mapCourtCode.mapCourtCodesForCourtTypeModel(COURT_TYPES, courtReturn));
    }

    @Test
    void shouldThrowExceptionWhenMappingCodeForCourtEntityForUnknownCourtType() {
        assertThatThrownBy(() -> mapCourtCode.mapCourtCodesForCourtEntity(INCORRECT_COURT_TYPES, court))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(UNKNOWN_COURT_TYPE_PREFIX + UNKNOWN_COURT_TYPE);
    }

    @Test
    void shouldThrowExceptionWhenMappingCodeForCourtTypeModelForUnknownCourtType() {
        assertThatThrownBy(() -> mapCourtCode.mapCourtCodesForCourtTypeModel(INCORRECT_COURT_TYPES, court))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(UNKNOWN_COURT_TYPE_PREFIX + UNKNOWN_COURT_TYPE);
    }

    @Test
    void shouldNotUpdateCourtCodeForTribunal() {
        List<CourtType> courtTypes = Collections.singletonList(new CourtType(1, "Tribunal", 100));
        mapCourtCode.mapCourtCodesForCourtEntity(courtTypes, court);
        assertNullCourtCode(court);
    }

    @Test
    void shouldNotUpdateCourtCodeForFamilyCourt() {
        List<CourtType> courtTypes = Collections.singletonList(new CourtType(1, "Family Court", 100));
        mapCourtCode.mapCourtCodesForCourtTypeModel(courtTypes, court);
        assertNullCourtCode(court);
    }

    private void assertNullCourtCode(final Court court) {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(court.getMagistrateCode()).isNull();
        softly.assertThat(court.getCciCode()).isNull();
        softly.assertThat(court.getNumber()).isNull();
        softly.assertAll();
    }
}
