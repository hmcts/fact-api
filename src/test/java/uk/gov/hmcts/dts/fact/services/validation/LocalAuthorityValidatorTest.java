package uk.gov.hmcts.dts.fact.services.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.services.MapitService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LocalAuthorityValidator.class)
public class LocalAuthorityValidatorTest {

    @MockBean
    private MapitService mockMapitService;

    @Autowired
    private LocalAuthorityValidator localAuthorityValidator;

    @Test
    public void testWhenLocalAuthorityIsValid() {
        when(mockMapitService.localAuthorityExists(Mockito.anyString())).thenReturn(true);
        assertTrue(localAuthorityValidator.localAuthorityExists("Birmingham City Council"));
    }

    @Test
    public void testIfPostcodesAreValid() {
        when(mockMapitService.localAuthorityExists(Mockito.anyString())).thenReturn(false);
        assertFalse(localAuthorityValidator.localAuthorityExists("Brnimgham City Councl"));
    }
}
