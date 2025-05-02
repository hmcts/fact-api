package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.Service;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.ServiceRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ServiceService.class)
class ServiceServiceTest {

    private static final String SERVICE_NAME = "serviceName";

    @Autowired
    private ServiceService serviceService;

    @MockitoBean
    private ServiceRepository serviceRepository;

    @Test
    void shouldThrowNotFoundException() {
        when(serviceRepository.findBySlugIgnoreCase(any())).thenReturn(empty());
        assertThrows(NotFoundException.class, () -> serviceService.getService("nonExistent"));
    }

    @Test
    void shouldReturnServiceObject() {
        final Service mock = mock(Service.class);
        when(serviceRepository.findBySlugIgnoreCase(SERVICE_NAME)).thenReturn(Optional.of(mock));
        assertThat(serviceService.getService(SERVICE_NAME)).isInstanceOf(uk.gov.hmcts.dts.fact.model.Service.class);
    }

    @Test
    void shouldReturnListOfServices() {
        final Service mock = mock(Service.class);
        final List<Service> services = singletonList(mock);
        when(serviceRepository.findAll()).thenReturn(services);

        final List<uk.gov.hmcts.dts.fact.model.Service> allServices = serviceService.getAllServices();
        assertThat(allServices).hasSize(1);
        assertThat(allServices.get(0)).isInstanceOf(uk.gov.hmcts.dts.fact.model.Service.class);
    }

    @Test
    void shouldReturnListOfServiceAreas() {
        final Service service = mock(Service.class);
        final ServiceArea serviceArea = mock(ServiceArea.class);
        final List<ServiceArea> servicesAreas = singletonList(serviceArea);
        final AreaOfLaw areaOfLaw = mock(AreaOfLaw.class);

        when(serviceRepository.findBySlugIgnoreCase(SERVICE_NAME)).thenReturn(Optional.of(service));
        when(service.getServiceAreas()).thenReturn(servicesAreas);
        when(serviceArea.getAreaOfLaw()).thenReturn(areaOfLaw);

        final List<uk.gov.hmcts.dts.fact.model.ServiceArea> allServices = serviceService.getServiceAreas(SERVICE_NAME);
        assertThat(allServices).hasSize(1);
        assertThat(allServices.get(0)).isInstanceOf(uk.gov.hmcts.dts.fact.model.ServiceArea.class);
    }
}
