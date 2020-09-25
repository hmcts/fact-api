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

    private final Court courtEntity = new Court();


    @Test
    void findCourtBySlug() throws Exception {
        courtEntity.setAddresses(emptyList());
        courtEntity.setAreasOfLaw(emptyList());
        courtEntity.setContacts(emptyList());
        courtEntity.setCourtTypes(emptyList());
        courtEntity.setEmails(emptyList());
        courtEntity.setFacilities(emptyList());
        courtEntity.setOpeningTimes(emptyList());
        final String searchSlug = "some-slug";
        uk.gov.hmcts.dts.fact.model.Court court = new uk.gov.hmcts.dts.fact.model.Court(courtEntity);
        when(courtService.getCourtBySlug(searchSlug)).thenReturn(court);
        MvcResult response = mockMvc.perform(get(String.format(URL, searchSlug)))
            .andExpect(status().isOk()).andReturn();
        assertThat(response != null);
        //TODO Add real test
    }
}
