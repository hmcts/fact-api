package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AreaOfLawTest {

    static uk.gov.hmcts.dts.fact.entity.AreaOfLaw entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.AreaOfLaw();
        entity.setName("Name of area of law");
        entity.setExternalLink("external link");
        entity.setExternalLinkCy("external link in Welsh");
        entity.setExternalLinkDescription("description of external link");
        entity.setExternalLinkDescriptionCy("description of external link in Welsh");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {

        AreaOfLaw areaOfLaw = new AreaOfLaw(entity, welsh);

        assertEquals(entity.getName(), areaOfLaw.getName());
        assertEquals(welsh ? entity.getExternalLinkCy() : entity.getExternalLink(), areaOfLaw.getExternalLink());
        assertEquals(
            welsh ? entity.getExternalLinkDescriptionCy() : entity.getExternalLinkDescription(),
            areaOfLaw.getExternalLinkDescription()
        );
    }

}
