package uk.gov.hmcts.dts.fact.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.ServiceArea;
import uk.gov.hmcts.dts.fact.services.ServiceAreaService;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceAreasController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceAreasControllerTest {

    private static final String URL = "/service-areas";
    private static final String NON_EXISTENT = "nonExistent";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private ServiceAreaService serviceAreaService;

    @Test
    void shouldGetAServiceArea() throws Exception {

        final Path path = Paths.get("src/integrationTest/resources/service-area.json");
        final ServiceArea serviceArea = new ObjectMapper().readValue(path.toFile(), ServiceArea.class);
        final String expected = new String(readAllBytes(path));

        when(serviceAreaService.getServiceArea(matches("adoption"))).thenReturn(serviceArea);
        mockMvc.perform(get(URL + "/adoption"))
            .andExpect(status().isOk())
            .andExpect(content().json(expected));
    }

    @Test
    void shouldReturn404WhenServiceNotFound() throws Exception {
        when(serviceAreaService.getServiceArea(matches(NON_EXISTENT))).thenThrow(new NotFoundException(NON_EXISTENT));
        mockMvc.perform(get(URL + "/nonExistent")).andExpect(status().is(NOT_FOUND.value()));
    }
}
