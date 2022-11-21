package uk.gov.hmcts.dts.fact.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CourtLocalAuthorityAreaOfLawTest {
    @Test
    void testCourtLocalAuthorityAreaOfLawTimeUpdated() {
        final CourtLocalAuthorityAreaOfLaw courtLocalAuthorityAreaOfLaw =
            new CourtLocalAuthorityAreaOfLaw(new AreaOfLaw(), new Court(), new LocalAuthority());
        Timestamp timestampNow = Timestamp.valueOf(LocalDateTime.of(2010, 8, 10, 2, 20));
        courtLocalAuthorityAreaOfLaw.updateTimestamp();
        assertThat(courtLocalAuthorityAreaOfLaw.getCourt().getUpdatedAt().getTime())
            .isGreaterThan(timestampNow.getTime());
    }
}
