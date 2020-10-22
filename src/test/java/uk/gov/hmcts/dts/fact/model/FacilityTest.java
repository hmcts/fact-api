package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FacilityTest {
    static uk.gov.hmcts.dts.fact.entity.Facility entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.Facility();
        entity.setName("A name");
        entity.setDescription("A description");
        entity.setDescriptionCy("A description in Welsh");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        Facility facility = new Facility(entity, welsh);
        assertEquals(entity.getName(), facility.getName());
        assertEquals(welsh ? entity.getDescriptionCy() : entity.getDescription(), facility.getDescription());
    }

}
