package uk.gov.hmcts.dts.fact.util;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapCourtCodeTest {


    private final MapCourtCode mapCourtCode = new MapCourtCode();



    private static final List<CourtType>  COURT_TYPES = Arrays.asList(
        new CourtType(1, "Magistrates' Court", 100),
        new CourtType(2,"County Court",101),
        new CourtType(3, "Crown Court",102)
    );


    private final Court court = new Court();
    private final Court courtReturn = mapCourtCode.mapCourtCodesForCourtEntity(COURT_TYPES,court);



    @Test
    void shouldReturnMapCourtCodesForCourtEntity() {

        assertEquals(court, mapCourtCode.mapCourtCodesForCourtEntity(COURT_TYPES,court));
    }


    @Test
    void shouldMapCourtCodesForCourtTypeModel() {

        assertEquals(COURT_TYPES, mapCourtCode.mapCourtCodesForCourtTypeModel(COURT_TYPES,courtReturn));
    }

}
