package uk.gov.hmcts.dts.fact.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CourtTest {
    @Test
    void testIsInPerson() {
        final Court court = new Court();
        court.setInPerson(null);
        assertThat(court.isInPerson()).isTrue();

        final InPerson inPerson = new InPerson();
        inPerson.setIsInPerson(true);
        court.setInPerson(inPerson);
        assertThat(court.isInPerson()).isTrue();

        inPerson.setIsInPerson(false);
        court.setInPerson(inPerson);
        assertThat(court.isInPerson()).isFalse();
    }
}
