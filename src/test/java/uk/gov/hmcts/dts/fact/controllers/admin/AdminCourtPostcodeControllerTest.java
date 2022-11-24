package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.exception.PostcodeExistedException;
import uk.gov.hmcts.dts.fact.exception.PostcodeNotFoundException;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtPostcodeService;
import uk.gov.hmcts.dts.fact.services.validation.ValidationService;
import uk.gov.hmcts.dts.fact.util.MvcSecurityUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;

@WebMvcTest(AdminCourtPostcodeController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("PMD.TooManyMethods")
public class AdminCourtPostcodeControllerTest {
    private static final String BASE_PATH = "/admin/courts/";
    private static final String POSTCODE_PATH = "/postcodes";
    private static final String TEST_SLUG = "test-slug";
    private static final String SOURCE_SLUG = "source-slug";
    private static final String DESTINATION_SLUG = "destination-slug";
    private static final String NOT_FOUND = "Not found: ";
    private static final String MESSAGE = "{\"message\":\"%s\"}";
    private static final String TEST_POSTCODE1 = "M11AA";
    private static final String TEST_POSTCODE2 = "M11BB";
    private static final String TEST_POSTCODE3 = "M11CC";
    private static final String TEST_USER = "mosh@cat.com";
    private static final List<String> TEST_POSTCODES = Arrays.asList(TEST_POSTCODE1, TEST_POSTCODE2, TEST_POSTCODE3);
    private static final List<String> INVALID_POSTCODE = singletonList(TEST_POSTCODE3);
    private static final List<String> EXISTED_POSTCODE = singletonList(TEST_POSTCODE2);
    private static final List<String> NOT_FOUND_POSTCODE = singletonList(TEST_POSTCODE1);
    private static final String JSON_NOT_FOUND_TEST_SLUG = String.format(MESSAGE, NOT_FOUND + TEST_SLUG);
    private static final String JSON_NOT_FOUND_SOURCE_SLUG = String.format(MESSAGE, NOT_FOUND + SOURCE_SLUG);
    private static final String JSON_NOT_FOUND_DESTINATION_SLUG = String.format(MESSAGE, NOT_FOUND + DESTINATION_SLUG);
    private static final String JSON_INVALID_POSTCODE = String.format(MESSAGE, TEST_POSTCODE3);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static String expectedPostcodeJson;
    private static String expectedExistedPostcodesJson;
    private static String expectedNotFoundPostcodesJson;

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtPostcodeService adminService;

    @MockBean
    private ValidationService validationService;

    @MockBean
    private AdminCourtLockService adminCourtLockService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUpMvc() {
        mockMvc = new MvcSecurityUtil().getMockMvcSecurityConfig(FACT_ADMIN, context, TEST_USER);
    }

    @BeforeAll
    static void setUp() throws JsonProcessingException {
        expectedPostcodeJson = OBJECT_MAPPER.writeValueAsString(TEST_POSTCODES);
        expectedExistedPostcodesJson = OBJECT_MAPPER.writeValueAsString(EXISTED_POSTCODE);
        expectedNotFoundPostcodesJson = OBJECT_MAPPER.writeValueAsString(NOT_FOUND_POSTCODE);
    }

    @Test
    void shouldReturnCourtPostcodes() throws Exception {
        when(adminService.getCourtPostcodesBySlug(TEST_SLUG)).thenReturn(TEST_POSTCODES);

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + POSTCODE_PATH))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedPostcodeJson));
    }

    @Test
    void retrievePostcodesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(adminService.getCourtPostcodesBySlug(TEST_SLUG)).thenThrow(new NotFoundException(TEST_SLUG));

        mockMvc.perform(get(BASE_PATH + TEST_SLUG + POSTCODE_PATH))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));
    }

    @Test
    void addPostcodesShouldReturnCreatedStatus() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(Collections.emptyList());
        when(adminService.addCourtPostcodes(TEST_SLUG, TEST_POSTCODES)).thenReturn(TEST_POSTCODES);

        mockMvc.perform(post(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().json(expectedPostcodeJson));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void addPostcodesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(Collections.emptyList());
        doThrow(new NotFoundException(TEST_SLUG)).when(adminService).checkPostcodesDoNotExist(
            TEST_SLUG,
            TEST_POSTCODES
        );

        mockMvc.perform(post(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));

        verify(adminService, never()).addCourtPostcodes(any(), any());
        verify(adminCourtLockService, never()).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void addPostcodesShouldReturnBadRequestForInvalidPostcodes() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(INVALID_POSTCODE);

        mockMvc.perform(post(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(JSON_INVALID_POSTCODE));

        verify(adminService, never()).addCourtPostcodes(any(), any());
        verify(adminCourtLockService, never()).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void addPostcodesShouldReturnConflictForExistedPostcodes() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(Collections.emptyList());
        doThrow(new PostcodeExistedException(EXISTED_POSTCODE)).when(adminService).checkPostcodesDoNotExist(
            TEST_SLUG,
            TEST_POSTCODES
        );

        mockMvc.perform(post(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(content().json(expectedExistedPostcodesJson));

        verify(adminService, never()).addCourtPostcodes(any(), any());
        verify(adminCourtLockService, never()).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void deletePostcodesShouldReturnOkStatus() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(Collections.emptyList());
        when(adminService.deleteCourtPostcodes(TEST_SLUG, TEST_POSTCODES)).thenReturn(3);

        mockMvc.perform(delete(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("3"));

        verify(adminCourtLockService, times(1)).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void deletePostcodesShouldReturnNotFoundForUnknownCourtSlug() throws Exception {
        doThrow(new NotFoundException(TEST_SLUG)).when(adminService).checkPostcodesExist(TEST_SLUG, TEST_POSTCODES);

        mockMvc.perform(delete(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_TEST_SLUG));

        verify(adminService, never()).deleteCourtPostcodes(any(), any());
        verify(adminCourtLockService, never()).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void deletePostcodesShouldReturnBadRequestForInvalidPostcodes() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(INVALID_POSTCODE);

        mockMvc.perform(delete(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(JSON_INVALID_POSTCODE));

        verify(adminService, never()).deleteCourtPostcodes(any(), any());
        verify(adminCourtLockService, never()).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void deletePostcodesShouldReturnNotFoundForNonExistentPostcodes() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(Collections.emptyList());
        doThrow(new PostcodeNotFoundException(NOT_FOUND_POSTCODE)).when(adminService).checkPostcodesExist(
            TEST_SLUG,
            TEST_POSTCODES
        );

        mockMvc.perform(delete(BASE_PATH + TEST_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(expectedNotFoundPostcodesJson));

        verify(adminService, never()).deleteCourtPostcodes(any(), any());
        verify(adminCourtLockService, never()).updateCourtLock(TEST_SLUG, TEST_USER);
    }

    @Test
    void movePostcodesShouldReturnOkStatus() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(Collections.emptyList());
        when(adminService.moveCourtPostcodes(SOURCE_SLUG, DESTINATION_SLUG, TEST_POSTCODES)).thenReturn(TEST_POSTCODES);

        mockMvc.perform(put(BASE_PATH + "/" + SOURCE_SLUG + "/" + DESTINATION_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedPostcodeJson));

        verify(adminCourtLockService, times(1)).updateCourtLock(SOURCE_SLUG, TEST_USER);
    }

    @Test
    void movePostcodesShouldReturnNotFoundForUnknownSourceCourt() throws Exception {
        doThrow(new NotFoundException(SOURCE_SLUG))
            .when(adminService).moveCourtPostcodes(SOURCE_SLUG, DESTINATION_SLUG, TEST_POSTCODES);

        mockMvc.perform(put(BASE_PATH + "/" + SOURCE_SLUG + "/" + DESTINATION_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_SOURCE_SLUG));

        verify(adminCourtLockService, times(1)).updateCourtLock(SOURCE_SLUG, TEST_USER);
    }

    @Test
    void movePostcodesShouldReturnNotFoundForUnknownDestinationCourt() throws Exception {
        doThrow(new NotFoundException(DESTINATION_SLUG))
            .when(adminService).moveCourtPostcodes(SOURCE_SLUG, DESTINATION_SLUG, TEST_POSTCODES);

        mockMvc.perform(put(BASE_PATH + "/" + SOURCE_SLUG + "/" + DESTINATION_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(JSON_NOT_FOUND_DESTINATION_SLUG));

        verify(adminCourtLockService, times(1)).updateCourtLock(SOURCE_SLUG, TEST_USER);
    }

    @Test
    void movePostcodesShouldReturnBadRequestForInvalidPostcodes() throws Exception {
        when(validationService.validatePostcodes(TEST_POSTCODES)).thenReturn(INVALID_POSTCODE);

        mockMvc.perform(put(BASE_PATH + "/" + SOURCE_SLUG + "/" + DESTINATION_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(JSON_INVALID_POSTCODE));

        verify(adminService, never()).moveCourtPostcodes(SOURCE_SLUG, DESTINATION_SLUG, TEST_POSTCODES);
        verify(adminCourtLockService, never()).updateCourtLock(SOURCE_SLUG, TEST_USER);
    }

    @Test
    void movePostcodesShouldReturnNotFoundIfPostcodeDoesNotExistInSourceCourt() throws Exception {
        doThrow(new PostcodeNotFoundException(NOT_FOUND_POSTCODE))
            .when(adminService).moveCourtPostcodes(SOURCE_SLUG, DESTINATION_SLUG, TEST_POSTCODES);

        mockMvc.perform(put(BASE_PATH + "/" + SOURCE_SLUG + "/" + DESTINATION_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().json(expectedNotFoundPostcodesJson));

        verify(adminCourtLockService, times(1)).updateCourtLock(SOURCE_SLUG, TEST_USER);
    }

    @Test
    void movePostcodesShouldReturnConflictIfPostcodeExistedInDestinationCourt() throws Exception {
        doThrow(new PostcodeExistedException(EXISTED_POSTCODE))
            .when(adminService).moveCourtPostcodes(SOURCE_SLUG, DESTINATION_SLUG, TEST_POSTCODES);

        mockMvc.perform(put(BASE_PATH + "/" + SOURCE_SLUG + "/" + DESTINATION_SLUG + POSTCODE_PATH)
                            .with(csrf())
                            .content(expectedPostcodeJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(content().json(expectedExistedPostcodesJson));

        verify(adminCourtLockService, times(1)).updateCourtLock(SOURCE_SLUG, TEST_USER);
    }
}
