package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.Contact;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtContactService;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.util.TestHelper.getResourceAsJson;

@WebMvcTest(AdminCourtContactController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtContactControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String CONTACTS_PATH = "/" + "contacts";
    private static final String TEST_SLUG = "unknownSlug";
    private static final String TEST_CONTACTS_FILE = "contacts.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtContactService adminService;

    @Test
    void retrieveContactsShouldReturnCourtContacts() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_CONTACTS_FILE);
        final List<Contact> contacts = asList(OBJECT_MAPPER.readValue(expectedJson, Contact[].class));

        when(adminService.getCourtContactsBySlug(TEST_SLUG)).thenReturn(contacts);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + CONTACTS_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void retrieveContactsShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtContactsBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + CONTACTS_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Not found: " + TEST_SLUG));
    }

    @Test
    void updateContactsShouldReturnUpdatedCourtContacts() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_CONTACTS_FILE);
        final List<Contact> contacts = asList(OBJECT_MAPPER.readValue(expectedJson, Contact[].class));

        when(adminService.updateCourtContacts(TEST_SLUG, contacts)).thenReturn(contacts);

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CONTACTS_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void updateContactsShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        final String expectedJson = getResourceAsJson(TEST_CONTACTS_FILE);
        final List<Contact> contacts = asList(OBJECT_MAPPER.readValue(expectedJson, Contact[].class));

        when(adminService.updateCourtContacts(TEST_SLUG, contacts)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(put(BASE_PATH + TEST_SLUG + CONTACTS_PATH)
                            .content(expectedJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Not found: " + TEST_SLUG));
    }


}
