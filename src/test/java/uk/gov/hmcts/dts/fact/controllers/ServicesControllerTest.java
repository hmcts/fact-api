package uk.gov.hmcts.dts.fact.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.Service;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.services.ServiceService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServicesController.class)
class ServicesControllerTest {

    protected static final String URL = "/services";
    protected static final String NON_EXISTENT = "nonExistent";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private ServiceService serviceService;

    @Test
    void shouldGetServices() throws Exception {

        final Path path = Paths.get("src/integrationTest/resources/services.json");
        List<Service> services = asList(new ObjectMapper().readValue(path.toFile(), Service[].class));
        final String expected = new String(readAllBytes(path));

        when(serviceService.getAllServices()).thenReturn(services);
        mockMvc.perform(get(URL)).andExpect(status().isOk()).andExpect(content().json(expected));
    }

    @Test
    void shouldGetAService() throws Exception {

        final Path path = Paths.get("src/integrationTest/resources/service.json");
        Service service = new ObjectMapper().readValue(path.toFile(), Service.class);

        final String expected = new String(readAllBytes(path));

        when(serviceService.getService(matches("money"))).thenReturn(service);
        mockMvc.perform(get(URL + "/money")).andExpect(status().isOk()).andExpect(content().json(expected));
    }

    @Test
    void shouldReturn404WhenServiceNotFound() throws Exception {
        when(serviceService.getService(matches(NON_EXISTENT))).thenThrow(new NotFoundException(NON_EXISTENT));
        mockMvc.perform(get(URL + "/nonExistent")).andExpect(status().is(404));
    }

    @Test
    void shouldGetServiceAreas() throws Exception {

        final Path path = Paths.get("src/integrationTest/resources/service-areas.json");
        List<ServiceArea> serviceAreas = asList(new ObjectMapper().readValue(path.toFile(), ServiceArea[].class));

        final String expected = new String(readAllBytes(path));

        when(serviceService.getServiceAreas(matches("money"))).thenReturn(serviceAreas);
        mockMvc.perform(get(URL + "/money/service-areas")).andExpect(status().isOk()).andExpect(content().json(expected));
    }


    @Test
    void shouldReturn404() throws Exception {
        when(serviceService.getServiceAreas(matches(NON_EXISTENT))).thenThrow(new NotFoundException(NON_EXISTENT));
        mockMvc.perform(get(URL + "/nonExistent/service-areas")).andExpect(status().is(404));
    }
}
