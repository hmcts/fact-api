package uk.gov.hmcts.dts.fact.services.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.services.MapitService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LocalAuthorityValidator.class)
class LocalAuthorityValidatorTest {

    @MockitoBean
    private MapitService mockMapitService;

    @Autowired
    private LocalAuthorityValidator localAuthorityValidator;

    @Test
    void testWhenLocalAuthorityIsValid() {
        when(mockMapitService.localAuthorityExists(Mockito.anyString())).thenReturn(true);
        assertTrue(localAuthorityValidator.localAuthorityNameIsValid("Birmingham City Council"));
    }

    @Test
    void testWhenLocalAuthorityIsInvalid() {
        when(mockMapitService.localAuthorityExists(Mockito.anyString())).thenReturn(false);
        assertFalse(localAuthorityValidator.localAuthorityNameIsValid("Brnimgham City Councl"));
    }
}
