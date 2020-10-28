package uk.gov.hmcts.dts.fact.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.Service;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.ServiceRepository;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ServiceService.class)
class ServiceServiceTest {

    @Autowired
    ServiceService serviceService;

    @MockBean
    ServiceRepository serviceRepository;


    @Test
    void shouldThrowSlugNotFoundException() {
        when(serviceRepository.findByNameIgnoreCase(any())).thenReturn(empty());
        assertThrows(NotFoundException.class, () -> serviceService.getService("nonExistent"));
    }

    @Test
    void shouldReturnServiceObject() {
        final Service mock = mock(Service.class);
        when(serviceRepository.findByNameIgnoreCase("serviceName")).thenReturn(Optional.of(mock));
        assertThat(serviceService.getService("serviceName")).isInstanceOf(uk.gov.hmcts.dts.fact.model.Service.class);
    }
}
