package uk.gov.hmcts.dts.fact.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CourtFacilityTest {

    @Test
    void testCourtFacilityTimeUpdated() {
        final CourtFacility courtFacility = new CourtFacility(new Court(), new Facility());
        Timestamp timestampNow = Timestamp.valueOf(LocalDateTime.now());
        courtFacility.updateTimestamp();
        assertThat(courtFacility.getCourt().getUpdatedAt().getTime()).isGreaterThan(timestampNow.getTime());
    }
}
