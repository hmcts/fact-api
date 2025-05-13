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
import uk.gov.hmcts.dts.fact.model.admin.OpeningType;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminOpeningTypeService;

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

@WebMvcTest(AdminOpeningTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminOpeningTypeControllerTest {
    private static final String BASE_PATH = "/admin/openingTypes";
    private static final Integer ID = 100;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockitoBean
    private AdminOpeningTypeService openingTypeService;

    private static final List<OpeningType> OPENING_TYPES = Arrays.asList(
        new OpeningType(100,"type1","type1Cy"),
        new OpeningType(200,"type2","type2Cy"),
        new OpeningType(300,"type3","type3Cy")
    );

    @Test
    void shouldReturnAllOpeningTypes() throws Exception {
        when(openingTypeService.getAllOpeningTypes()).thenReturn(OPENING_TYPES);

        final String allContactTypeJson = OBJECT_MAPPER.writeValueAsString(OPENING_TYPES);

        mockMvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(allContactTypeJson));
    }

    @Test
    void shouldReturnAOpeningType() throws Exception {
        final OpeningType openingType = OPENING_TYPES.get(0);
        when(openingTypeService.getOpeningType(ID)).thenReturn(openingType);

        final String contactTypeJson = OBJECT_MAPPER.writeValueAsString(openingType);

        mockMvc.perform(get(BASE_PATH  + "/" + ID).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(contactTypeJson));
    }

    @Test
    void shouldReturnNotFoundWhenOpeningTypeIdNotFound() throws Exception {
        when(openingTypeService.getOpeningType(400)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(BASE_PATH + "/400").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateAndReturnUpdatedOpeningType() throws Exception {
        final OpeningType openingType = OPENING_TYPES.get(0);
        when(openingTypeService.updateOpeningType(openingType)).thenReturn(openingType);

        final String contactTypeJson = OBJECT_MAPPER.writeValueAsString(openingType);

        mockMvc.perform(put(BASE_PATH)
                            .content(contactTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(contactTypeJson));
    }

    @Test
    void shouldReturnNotFoundForUnknownOpeningType() throws Exception {
        final OpeningType openingType = OPENING_TYPES.get(0);
        when(openingTypeService.updateOpeningType(openingType)).thenThrow(NotFoundException.class);

        final String contactTypeJson = OBJECT_MAPPER.writeValueAsString(openingType);

        mockMvc.perform(put(BASE_PATH)
                            .content(contactTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateAndReturnNewOpeningType() throws Exception {
        final OpeningType openingType = OPENING_TYPES.get(0);
        when(openingTypeService.createOpeningType(openingType)).thenReturn(openingType);

        final String contactTypeJson = OBJECT_MAPPER.writeValueAsString(openingType);

        mockMvc.perform(post(BASE_PATH)
                            .content(contactTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().json(contactTypeJson));
    }

    @Test
    void shouldReturnConflictIfOpeningTypeAlreadyExists() throws Exception {
        final OpeningType openingType = OPENING_TYPES.get(0);
        when(openingTypeService.createOpeningType(openingType)).thenThrow(DuplicatedListItemException.class);

        final String contactTypeJson = OBJECT_MAPPER.writeValueAsString(openingType);

        mockMvc.perform(post(BASE_PATH)
                            .content(contactTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }



    @Test
    void shouldSuccessfullyDeleteOpeningType() throws Exception {
        final String idJson = OBJECT_MAPPER.writeValueAsString(ID);


        mockMvc.perform(delete(BASE_PATH  + "/" + ID)
                            .content(OBJECT_MAPPER.writeValueAsString(idJson))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(idJson));
    }

    @Test
    void shouldReturnConflictIfOpeningTypeInUse() throws Exception {
        doThrow(mock(ListItemInUseException.class)).when(openingTypeService).deleteOpeningType(ID);

        mockMvc.perform(delete(BASE_PATH  + "/" + ID)
                            .content(OBJECT_MAPPER.writeValueAsString(ID))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Test
    void deleteContactTypeShouldReturnNotFoundIfIdDoesNotExist() throws Exception {
        doThrow(mock(NotFoundException.class)).when(openingTypeService).deleteOpeningType(ID);

        mockMvc.perform(delete(BASE_PATH  + "/" + ID)
                            .content(OBJECT_MAPPER.writeValueAsString(ID))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }



}
