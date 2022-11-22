package uk.gov.hmcts.dts.fact.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CourtFacilityTest {

    @Test
    void testCourtFacilityTimeUpdated() {
        final CourtFacility courtFacility = new CourtFacility(new Court(), new Facility());
        Timestamp timestampNow = Timestamp.valueOf(LocalDateTime.of(2010, 8, 10, 2, 20));
        courtFacility.updateTimestamp();
        assertThat(courtFacility.getCourt().getUpdatedAt().getTime()).isGreaterThan(timestampNow.getTime());
    }
}
