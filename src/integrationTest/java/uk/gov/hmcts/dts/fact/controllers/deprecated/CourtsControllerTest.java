package uk.gov.hmcts.dts.fact.controllers.deprecated;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourtsController.class)
class CourtsControllerTest {


    protected static final String URL = "/courts/%s.json";

    @Autowired
    private transient MockMvc mockMvc;

    @Test
    void findCourtBySlug() throws Exception {

        final String searchSlug = "some-slug";

        mockMvc.perform(get(String.format(URL, searchSlug)))
            .andExpect(status().isNotImplemented())
            .andExpect(content().string(equalTo("Not yet implemented")));
    }
}
