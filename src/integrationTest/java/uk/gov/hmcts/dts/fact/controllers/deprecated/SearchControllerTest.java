package uk.gov.hmcts.dts.fact.controllers.deprecated;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    private static final String URL = "/search/results.json";

    @Autowired
    private transient MockMvc mockMvc;

    @Test
    void findCourtBySlug() throws Exception {
        mockMvc.perform(
            get(URL)
        )
            .andExpect(status().isNotImplemented())
            .andExpect(content().string(equalTo("Not yet implemented")));
    }

}
