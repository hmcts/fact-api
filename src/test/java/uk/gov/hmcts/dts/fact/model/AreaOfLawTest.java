package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AreaOfLawTest {

    @Test
    public void testCreation() {
        uk.gov.hmcts.dts.fact.entity.AreaOfLaw entity = new uk.gov.hmcts.dts.fact.entity.AreaOfLaw();
        entity.setName("Name of area of law");
        entity.setExternalLink("external link");
        entity.setExternalLinkDescription("description of external link");

        AreaOfLaw areaOfLaw = new AreaOfLaw(entity);

        assertEquals(entity.getName(), areaOfLaw.getName());
        assertEquals(entity.getExternalLink(), areaOfLaw.getExternalLink());
        assertEquals(entity.getExternalLinkDescription(), areaOfLaw.getExternalLinkDescription());
    }

}
