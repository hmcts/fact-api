package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ServiceAreaService.class)
class ServiceAreaServiceTest {

    @Autowired
    private ServiceAreaService serviceAreaService;

    @MockitoBean
    private ServiceAreaRepository serviceAreaRepository;

    @Test
    void shouldReturnServiceAreaObject() {
        final String serviceAreaSlug = "money";
        final ServiceArea serviceArea = mock(ServiceArea.class);
        final AreaOfLaw areaOfLaw = mock(AreaOfLaw.class);

        when(serviceArea.getAreaOfLaw()).thenReturn(areaOfLaw);
        when(serviceAreaRepository.findBySlugIgnoreCase(serviceAreaSlug)).thenReturn(Optional.of(serviceArea));

        assertThat(serviceAreaService.getServiceArea(serviceAreaSlug)).isInstanceOf(uk.gov.hmcts.dts.fact.model.ServiceArea.class);
    }

    @Test
    void shouldReturnAllServiceAreasObject() {
        when(serviceAreaRepository.findAllByNameIn(any())).thenReturn(Optional.empty());
        assertThat(serviceAreaService.getAllServiceAreas()).isInstanceOf(List.class);
    }
}
