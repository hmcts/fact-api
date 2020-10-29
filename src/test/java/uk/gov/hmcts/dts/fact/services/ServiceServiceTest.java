package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
    ServiceService serviceService;

    @MockBean
    ServiceRepository serviceRepository;


    @Test
    void shouldThrowNotFoundException() {
        when(serviceRepository.findByNameIgnoreCase(any())).thenReturn(empty());
        assertThrows(NotFoundException.class, () -> serviceService.getService("nonExistent"));
    }

    @Test
    void shouldReturnServiceObject() {
        final Service mock = mock(Service.class);
        when(serviceRepository.findByNameIgnoreCase(SERVICE_NAME)).thenReturn(Optional.of(mock));
        assertThat(serviceService.getService(SERVICE_NAME)).isInstanceOf(uk.gov.hmcts.dts.fact.model.Service.class);
    }

    @Test
    void shouldReturnListOfServices() {
        Service mock = mock(Service.class);
        List<Service> services = singletonList(mock);
        when(serviceRepository.findAll()).thenReturn(services);
        final List<uk.gov.hmcts.dts.fact.model.Service> allServices = serviceService.getAllServices();
        assertThat(allServices.size()).isEqualTo(1);
        assertThat(allServices.get(0)).isInstanceOf(uk.gov.hmcts.dts.fact.model.Service.class);
    }

    @Test
    void shouldReturnListOfServiceAreas() {
        final Service serviceMock = mock(Service.class);
        ServiceArea serviceAreaMock = mock(ServiceArea.class);
        List<ServiceArea> servicesAreas = singletonList(serviceAreaMock);
        when(serviceRepository.findByNameIgnoreCase(SERVICE_NAME)).thenReturn(Optional.of(serviceMock));
        when(serviceMock.getServiceAreas()).thenReturn(servicesAreas);
        final List<uk.gov.hmcts.dts.fact.model.ServiceArea> allServices = serviceService.getServiceAreas(SERVICE_NAME);
        assertThat(allServices.size()).isEqualTo(1);
        assertThat(allServices.get(0)).isInstanceOf(uk.gov.hmcts.dts.fact.model.ServiceArea.class);
    }
}
