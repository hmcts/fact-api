package uk.gov.hmcts.dts.fact.controllers.deprecated;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.services.CourtService;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourtsController.class)
class CourtsControllerTest {

    protected static final String URL = "/courts/%s.json";

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private CourtService courtService;

    private final Court court = new Court();


    @Test
    void findCourtBySlug() throws Exception {
        court.setAddresses(emptyList());
        court.setAreasOfLaw(emptyList());
        court.setContacts(emptyList());
        court.setCourtTypes(emptyList());
        court.setEmails(emptyList());
        court.setFacilities(emptyList());
        court.setOpeningTimes(emptyList());
        final String searchSlug = "some-slug";
        when(courtService.getCourtBySlug(searchSlug)).thenReturn(court);
        MvcResult response = mockMvc.perform(get(String.format(URL, searchSlug)))
            .andExpect(status().isOk()).andReturn();
        assertThat(response != null);
        //TODO Add real test
    }
}
