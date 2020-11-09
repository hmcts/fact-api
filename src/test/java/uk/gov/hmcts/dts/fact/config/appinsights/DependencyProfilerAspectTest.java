package uk.gov.hmcts.dts.fact.config.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DependencyProfilerAspect.class)
class DependencyProfilerAspectTest {

    @MockBean
    TelemetryClient client;

    @MockBean
    DependencyProfiler dependencyProfiler;

    @MockBean
    ProceedingJoinPoint join;

    @Autowired
    DependencyProfilerAspect aspect;

    @Test
    public void testDependencyTrack() throws Throwable {
        when(dependencyProfiler.action()).thenReturn("callApi");
        when(dependencyProfiler.name()).thenReturn("dependency");
        doNothing().when(client).trackDependency(any(), any(), any(), anyBoolean());

        aspect.logExecutionTime(join, dependencyProfiler);

        verify(client, atLeastOnce()).trackDependency(any(), any(), any(), anyBoolean());
    }

}
