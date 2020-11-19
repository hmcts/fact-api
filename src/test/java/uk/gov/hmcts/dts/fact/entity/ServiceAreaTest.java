package uk.gov.hmcts.dts.fact.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServiceAreaTest {

    @Test
    void shouldReturnTrueIfHasRegionalCourts() {
        final ServiceArea serviceArea = new ServiceArea();
        final ServiceAreaCourt serviceAreaCourt1 = mock(ServiceAreaCourt.class);
        final ServiceAreaCourt serviceAreaCourt2 = mock(ServiceAreaCourt.class);
        final List<ServiceAreaCourt> serviceAreaCourts = asList(serviceAreaCourt1, serviceAreaCourt2);
        serviceArea.setServiceAreaCourts(serviceAreaCourts);

        when(serviceAreaCourt1.getCatchmentType()).thenReturn("local");
        when(serviceAreaCourt2.getCatchmentType()).thenReturn("regional");

        assertTrue(serviceArea.isRegional());
    }

    @Test
    void shouldReturnFalseIfNoRegionalCourts() {
        final ServiceArea serviceArea = new ServiceArea();
        final ServiceAreaCourt serviceAreaCourt1 = mock(ServiceAreaCourt.class);
        final ServiceAreaCourt serviceAreaCourt2 = mock(ServiceAreaCourt.class);
        final List<ServiceAreaCourt> serviceAreaCourts = asList(serviceAreaCourt1, serviceAreaCourt2);
        serviceArea.setServiceAreaCourts(serviceAreaCourts);

        when(serviceAreaCourt1.getCatchmentType()).thenReturn("local");
        when(serviceAreaCourt2.getCatchmentType()).thenReturn("local");

        assertFalse(serviceArea.isRegional());
    }
}