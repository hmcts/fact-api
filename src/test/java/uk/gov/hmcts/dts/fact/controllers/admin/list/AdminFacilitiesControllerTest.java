package uk.gov.hmcts.dts.fact.controllers.admin.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.ListItemInUseException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.FacilityType;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminFacilityService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("PMD.TooManyMethods")
@WebMvcTest(AdminFacilitiesController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminFacilitiesControllerTest {

    private static final String BASE_PATH = "/admin/facilities";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminFacilityService adminFacilityService;

    @Test
    void shouldReturnAllFacilities() throws Exception {

        final uk.gov.hmcts.dts.fact.entity.FacilityType facilityType1 = new uk.gov.hmcts.dts.fact.entity.FacilityType();
        facilityType1.setId(1);
        facilityType1.setName("FacilityType1");
        facilityType1.setOrder(1);
        final uk.gov.hmcts.dts.fact.entity.FacilityType facilityType2 = new uk.gov.hmcts.dts.fact.entity.FacilityType();
        facilityType2.setId(2);
        facilityType2.setName("FacilityType2");
        facilityType2.setOrder(2);
        final uk.gov.hmcts.dts.fact.entity.FacilityType facilityType3 = new uk.gov.hmcts.dts.fact.entity.FacilityType();
        facilityType3.setId(3);
        facilityType3.setName("FacilityType3");
        facilityType3.setOrder(3);


        final List<FacilityType> mockFacilityTypes = Arrays.asList(
            new FacilityType(facilityType1),
            new FacilityType(facilityType2),
            new FacilityType(facilityType3)
        );

        when(adminFacilityService.getAllFacilityTypes()).thenReturn(mockFacilityTypes);

        final String allFacilityTypesJson = OBJECT_MAPPER.writeValueAsString(mockFacilityTypes);

        mockMvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(allFacilityTypesJson));
    }

    @Test
    void shouldReturnFacilityTypeForAGivenId() throws Exception {
        final Integer id = 100;
        final FacilityType facilityType = getFacilityType(id, "Parking", "Parcio");

        when(adminFacilityService.getFacilityType(id)).thenReturn(facilityType);

        final String facilityTypeJson = OBJECT_MAPPER.writeValueAsString(facilityType);

        mockMvc.perform(get(BASE_PATH + "/" + id)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(facilityTypeJson));
    }

    @Test
    void shouldReturnNotFoundIfIdDoesNotExist() throws Exception {
        final Integer id = 234;
        when(adminFacilityService.getFacilityType(id)).thenThrow(new NotFoundException(id.toString()));

        mockMvc.perform(get(BASE_PATH + "/" + id)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateExistingFacilityType() throws Exception {
        final FacilityType existingFacilityType = getFacilityType(900, "Lift", "Lifft");

        when(adminFacilityService.updateFacilityType(existingFacilityType)).thenReturn(existingFacilityType);

        final String facilityTypeJson = OBJECT_MAPPER.writeValueAsString(existingFacilityType);

        mockMvc.perform(put(BASE_PATH)
                            .content(facilityTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(facilityTypeJson));
    }

    @Test
    void updateShouldReturnConflictIfNameIsAlreadyInUse() throws Exception {
        final Integer id = 500;
        final FacilityType existingFacilityType = getFacilityType(id, "Video facilities", "Cyfleusterau fideo");

        when(adminFacilityService.updateFacilityType(existingFacilityType)).thenThrow(new DuplicatedListItemException(id.toString()));

        final String facilityTypeJson = OBJECT_MAPPER.writeValueAsString(existingFacilityType);

        mockMvc.perform(put(BASE_PATH)
                            .content(facilityTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Test
    void updateShouldReturnNotFoundIfFacilityTypeDoesNotExist() throws Exception {
        final Integer id = 500;
        final FacilityType existingFacilityType = getFacilityType(id, "Video facilities", "Cyfleusterau fideo");

        when(adminFacilityService.updateFacilityType(existingFacilityType)).thenThrow(new NotFoundException(id.toString()));

        final String facilityTypeJson = OBJECT_MAPPER.writeValueAsString(existingFacilityType);

        mockMvc.perform(put(BASE_PATH)
                            .content(facilityTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewFacilityType() throws Exception {
        final FacilityType newFacilityType = getFacilityType(1200, "NEW Facility Type", "NEW Facility Type (cy)");

        when(adminFacilityService.createFacilityType(newFacilityType)).thenReturn(newFacilityType);

        final String facilityTypeJson = OBJECT_MAPPER.writeValueAsString(newFacilityType);

        mockMvc.perform(post(BASE_PATH)
                            .content(facilityTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().json(facilityTypeJson));
    }

    @Test
    void createShouldReturnConflictIfNameIsAlreadyInUse() throws Exception {
        final FacilityType newFacilityType = getFacilityType(1200, "NEW Facility Type", "NEW Facility Type (cy)");

        when(adminFacilityService.createFacilityType(newFacilityType))
            .thenThrow(new DuplicatedListItemException(newFacilityType.getName()));

        final String facilityTypeJson = OBJECT_MAPPER.writeValueAsString(newFacilityType);

        mockMvc.perform(post(BASE_PATH)
                            .content(facilityTypeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Test
    void shouldDeleteFacilityType() throws Exception {
        final Integer idToDelete = 543;

        mockMvc.perform(delete(BASE_PATH + "/" + idToDelete).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void deleteShouldReturnConflictWhenFacilityTypeInUse() throws Exception {
        final Integer idInUse = 321;
        doThrow(mock(ListItemInUseException.class)).when(adminFacilityService).deleteFacilityType(idInUse);

        mockMvc.perform(delete(BASE_PATH + "/" + idInUse).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Test
    void deleteShouldReturnNotFoundResponseIfFacilityTypeDoesNotExist() throws Exception {
        final Integer idInUse = 321;
        doThrow(mock(NotFoundException.class)).when(adminFacilityService).deleteFacilityType(idInUse);

        mockMvc.perform(delete(BASE_PATH + "/" + idInUse).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReorderFacilityTypes() throws Exception {
        final List<FacilityType> facilityTypes = Arrays.asList(
            getFacilityType(30, "FT1", "FT1cy"),
            getFacilityType(20, "FT2", "FT2cy"),
            getFacilityType(10, "FT3", "FT3cy"));

        final List<Integer> newOrder = Arrays.asList(30, 10, 20);

        when(adminFacilityService.reorderFacilityTypes(newOrder)).thenReturn(facilityTypes);

        final String newOrderJson = OBJECT_MAPPER.writeValueAsString(newOrder);
        final String facilityTypesJson = OBJECT_MAPPER.writeValueAsString(facilityTypes);

        mockMvc.perform(put(BASE_PATH + "/reorder")
                            .content(newOrderJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(facilityTypesJson));
    }

    @Test
    void reorderShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        final List<Integer> idsInOrder = Arrays.asList(100, 200, 300);
        doThrow(mock(NotFoundException.class)).when(adminFacilityService).reorderFacilityTypes(idsInOrder);

        mockMvc.perform(put(BASE_PATH + "/reorder")
                            .content(OBJECT_MAPPER.writeValueAsString(idsInOrder))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    private FacilityType getFacilityType(Integer id, String name, String nameCy) {
        FacilityType facilityType = new FacilityType();
        facilityType.setId(id);
        facilityType.setName(name);
        facilityType.setNameCy(nameCy);
        facilityType.setOrder(3);
        return facilityType;
    }
}
