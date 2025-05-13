package uk.gov.hmcts.dts.fact.controllers.admin.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.ListItemInUseException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.ContactType;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminContactTypeService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminContactTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminContactTypeControllerTest {
    private static final String BASE_PATH = "/admin/contactTypes";
    private static final Integer ID = 100;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockitoBean
    private AdminContactTypeService adminContactTypeService;

    private static final List<ContactType> CONTACT_TYPES = Arrays.asList(
        new ContactType(100,"type1","type1Cy"),
        new ContactType(200,"type2","type2Cy"),
        new ContactType(300,"type3","type3Cy")
    );

    @Test
    void shouldReturnAllContactTypes() throws Exception {
        when(adminContactTypeService.getAllContactTypes()).thenReturn(CONTACT_TYPES);

        final String allContactTypeJson = OBJECT_MAPPER.writeValueAsString(CONTACT_TYPES);

        mockMvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(allContactTypeJson));
    }

    @Test
    void shouldReturnACourtContactType() throws Exception {
        final ContactType contactType = CONTACT_TYPES.get(0);
        when(adminContactTypeService.getContactType(100)).thenReturn(contactType);

        final String contactTypeJson = OBJECT_MAPPER.writeValueAsString(contactType);

        mockMvc.perform(get(BASE_PATH + "/100").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(contactTypeJson));
    }

    @Test
    void shouldReturnNotFoundWhenContactTypeIdNotFound() throws Exception {
        when(adminContactTypeService.getContactType(400)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(BASE_PATH + "/400").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateAndReturnUpdatedContactType() throws Exception {
        final ContactType contactType = CONTACT_TYPES.get(0);
        when(adminContactTypeService.updateContactType(contactType)).thenReturn(contactType);

        final String contactTypeJson = OBJECT_MAPPER.writeValueAsString(contactType);

        mockMvc.perform(put(BASE_PATH)
                            .content(contactTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(contactTypeJson));
    }

    @Test
    void shouldReturnNotFoundForUnknownContactType() throws Exception {
        final ContactType contactType = CONTACT_TYPES.get(0);
        when(adminContactTypeService.updateContactType(contactType)).thenThrow(NotFoundException.class);

        final String contactTypeJson = OBJECT_MAPPER.writeValueAsString(contactType);

        mockMvc.perform(put(BASE_PATH)
                            .content(contactTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateAndReturnNewContactType() throws Exception {
        final ContactType contactType = CONTACT_TYPES.get(0);
        when(adminContactTypeService.createContactType(contactType)).thenReturn(contactType);

        final String contactTypeJson = OBJECT_MAPPER.writeValueAsString(contactType);

        mockMvc.perform(post(BASE_PATH)
                            .content(contactTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().json(contactTypeJson));
    }

    @Test
    void shouldReturnConflictIfContactTypeAlreadyExists() throws Exception {
        final ContactType contactType = CONTACT_TYPES.get(0);
        when(adminContactTypeService.createContactType(contactType)).thenThrow(DuplicatedListItemException.class);

        final String contactTypeJson = OBJECT_MAPPER.writeValueAsString(contactType);

        mockMvc.perform(post(BASE_PATH)
                            .content(contactTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Test
    void shouldSuccessfullyDeleteContactType() throws Exception {
        final String idJson = OBJECT_MAPPER.writeValueAsString(ID);

        mockMvc.perform(delete(BASE_PATH  + "/" + ID)
                            .content(OBJECT_MAPPER.writeValueAsString(idJson))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(idJson));
    }

    @Test
    void shouldReturnConflictIfContactTypeInUse() throws Exception {
        doThrow(mock(ListItemInUseException.class)).when(adminContactTypeService).deleteContactType(ID);

        mockMvc.perform(delete(BASE_PATH  + "/" + ID)
                            .content(OBJECT_MAPPER.writeValueAsString(ID))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Test
    void deleteContactTypeShouldReturnNotFoundIfIdDoesNotExist() throws Exception {
        doThrow(mock(NotFoundException.class)).when(adminContactTypeService).deleteContactType(ID);

        mockMvc.perform(delete(BASE_PATH  + "/" + ID)
                            .content(OBJECT_MAPPER.writeValueAsString(ID))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
