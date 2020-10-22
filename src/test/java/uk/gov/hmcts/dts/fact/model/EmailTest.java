package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailTest {

    static uk.gov.hmcts.dts.fact.entity.Email entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.Email();
        entity.setAddress("test@test.com");
        entity.setDescription("An email address");
        entity.setExplanation("You email it.");
        entity.setExplanationCy("You email it in Welsh.");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        Email email = new Email(entity, welsh);
        assertEquals(entity.getAddress(), email.getAddress());
        assertEquals(entity.getDescription(), email.getDescription());
        assertEquals(welsh ? entity.getExplanationCy() : entity.getExplanation(), email.getExplanation());
    }

}
