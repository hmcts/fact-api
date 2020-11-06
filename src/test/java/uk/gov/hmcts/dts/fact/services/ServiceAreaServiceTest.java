package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ServiceAreaService.class)
public class ServiceAreaServiceTest {

    @Autowired
    private ServiceAreaService serviceAreaService;

    @MockBean
    private ServiceAreaRepository serviceAreaRepository;

    @Test
    void shouldReturnServiceAreaObject() {
        final String serviceAreaSlug = "money";
        final ServiceArea mock = mock(ServiceArea.class);
        when(serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug)).thenReturn(Optional.of(mock));

        assertThat(serviceAreaService.getServiceArea(serviceAreaSlug)).isInstanceOf(uk.gov.hmcts.dts.fact.model.ServiceArea.class);
    }
}
