package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourtEmailTest {

    @Test
    public void testCreation() {
        uk.gov.hmcts.dts.fact.entity.CourtEmail entity = new uk.gov.hmcts.dts.fact.entity.CourtEmail();
        entity.setAddress("test@test.com");
        entity.setDescription("An email address");
        entity.setExplanation("You email it.");

        CourtEmail email = new CourtEmail(entity);

        assertEquals(entity.getAddress(), email.getAddress());
        assertEquals(entity.getDescription(), email.getDescription());
        assertEquals(entity.getExplanation(), email.getExplanation());
    }

}
