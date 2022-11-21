package uk.gov.hmcts.dts.fact.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.dts.fact.entity.CourtLock;
import uk.gov.hmcts.dts.fact.services.admin.AdminCourtLockService;
import uk.gov.hmcts.dts.fact.util.MvcSecurityUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.dts.fact.services.admin.AdminRole.FACT_ADMIN;

@WebMvcTest(AdminCourtLockController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCourtLockControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private AdminCourtLockService adminCourtLockService;

    @Autowired
    private WebApplicationContext context;

    private static final String TEST_SLUG = "unknownSlug";
    private static final String TEST_USER = "mosh@cat.com";
    private static final String BASE_PATH = "/admin/courts/";
    private static final String CHILD_PATH = "/lock";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String TEST_SLUG_1 = "mosh-slug";
    private static final String TEST_SLUG_2 = "kupo-slug";
    private static final String TEST_USER_2 = "kupo@cat.com";
    private static final LocalDateTime TEST_LOCK_ACQUIRED_2 =
        LocalDateTime.of(2001, 8, 28, 20, 20);
    private static final CourtLock ENTITY_COURT_LOCK_1 = new CourtLock(
        1,
        LocalDateTime.of(2000, 8, 28, 20, 20),
        "mosh@cat.com",
        TEST_SLUG_1
    );
    private static final CourtLock ENTITY_COURT_LOCK_2 = new CourtLock(
        2,
        TEST_LOCK_ACQUIRED_2,
        TEST_USER_2,
        TEST_SLUG_2
    );
    private static final CourtLock ENTITY_COURT_LOCK_3 = new CourtLock(
        3,
        LocalDateTime.of(2002, 8, 28, 20, 20),
        "dil@cat.com",
        "dil-slug"
    );

    private static final List<uk.gov.hmcts.dts.fact.model.admin.CourtLock> EXPECTED_COURT_LOCK_LIST =
        Arrays.asList(
            new uk.gov.hmcts.dts.fact.model.admin.CourtLock(ENTITY_COURT_LOCK_1),
            new uk.gov.hmcts.dts.fact.model.admin.CourtLock(ENTITY_COURT_LOCK_2),
            new uk.gov.hmcts.dts.fact.model.admin.CourtLock(ENTITY_COURT_LOCK_3)
        );

    @BeforeEach
    public void setUpMvc() {
        mockMvc = new MvcSecurityUtil().getMockMvcSecurityConfig(FACT_ADMIN, context, TEST_USER);
    }

    @BeforeAll
    public static void beforeAll() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldReturnCourtLocksSuccess() throws Exception {
        when(adminCourtLockService.getCourtLocks(TEST_SLUG)).thenReturn(
            EXPECTED_COURT_LOCK_LIST);

        MvcResult mvcResult = mockMvc.perform(get(BASE_PATH + TEST_SLUG + CHILD_PATH))
            .andExpect(status().isOk()).andReturn();

        assertThat(Arrays.asList(OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(),
                                                         uk.gov.hmcts.dts.fact.model.admin.CourtLock[].class)))
            .isEqualTo(EXPECTED_COURT_LOCK_LIST);
    }

    @Test
    void addCourtLockSuccess() throws Exception {
        when(adminCourtLockService.addNewCourtLock(new uk.gov.hmcts.dts.fact.model.admin.CourtLock(ENTITY_COURT_LOCK_1)))
            .thenReturn(new uk.gov.hmcts.dts.fact.model.admin.CourtLock(ENTITY_COURT_LOCK_1));

        MvcResult mvcResult = mockMvc.perform(post(BASE_PATH + TEST_SLUG + CHILD_PATH)
                                                  .with(csrf())
                                                  .content(OBJECT_MAPPER.writeValueAsString(EXPECTED_COURT_LOCK_LIST.get(0)))
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated()).andReturn();

        assertThat(OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(),
                                                         uk.gov.hmcts.dts.fact.model.admin.CourtLock.class))
            .isEqualTo(EXPECTED_COURT_LOCK_LIST.get(0));
    }

    @Test
    void deleteCourtLockSuccess() throws Exception {
        when(adminCourtLockService.deleteCourtLock(TEST_SLUG_2, TEST_USER_2))
            .thenReturn(Collections.singletonList(new uk.gov.hmcts.dts.fact.model.admin.CourtLock(ENTITY_COURT_LOCK_2)));

        MvcResult mvcResult = mockMvc.perform(delete(BASE_PATH + TEST_SLUG_2 + CHILD_PATH + "/" + TEST_USER_2)
                                                  .with(csrf()))
            .andExpect(status().isOk()).andReturn();

        assertThat(Arrays.asList(OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(),
                                                         uk.gov.hmcts.dts.fact.model.admin.CourtLock[].class)))
            .isEqualTo(Collections.singletonList(EXPECTED_COURT_LOCK_LIST.get(1)));
    }

    @Test
    void deleteCourtLockByEmailSuccess() throws Exception {
        when(adminCourtLockService.deleteCourtLockByEmail(TEST_USER_2))
            .thenReturn(Collections.singletonList(new uk.gov.hmcts.dts.fact.model.admin.CourtLock(ENTITY_COURT_LOCK_2)));

        MvcResult mvcResult = mockMvc.perform(delete(BASE_PATH + TEST_USER_2 + CHILD_PATH)
                                                  .with(csrf()))
            .andExpect(status().isOk()).andReturn();

        assertThat(Arrays.asList(OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(),
                                                         uk.gov.hmcts.dts.fact.model.admin.CourtLock[].class)))
            .isEqualTo(Collections.singletonList(EXPECTED_COURT_LOCK_LIST.get(1)));
    }
}
