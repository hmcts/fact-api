package uk.gov.hmcts.dts.fact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.FileOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void contextLoads() {
    }

    @Test
    public void welcomeRootEndpoint() throws Exception {
        MvcResult response = mockMvc.perform(get("/")).andExpect(status().isOk()).andReturn();

        assertThat(response.getResponse().getContentAsString()).startsWith("Welcome");
    }

    @Test
    void should_generate_swagger_docs() throws Exception {
        byte[] specs = mockMvc.perform(get("/v2/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

        FileOutputStream outputStream = new FileOutputStream("/tmp/swagger-specs.json");
        outputStream.write(specs);
        outputStream.close();
    }
}
