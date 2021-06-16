package uk.gov.hmcts.dts.fact.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.mapit.MapItService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ValidationController.class)
public class ValidationControllerTest {

    private static final String URL = "/validate/postcodes";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private MapItService mapitService;

    @Test
    void shouldGetInvalidPostcodes() throws Exception {

        String[] invalidPostcodes = {"ABC", "123", "A", "MO"};
        String json = new ObjectMapper().writeValueAsString(invalidPostcodes);
        when(mapitService.validatePostcodes(any())).thenReturn(invalidPostcodes);
        mockMvc.perform(post(URL)
                            .content(json)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
