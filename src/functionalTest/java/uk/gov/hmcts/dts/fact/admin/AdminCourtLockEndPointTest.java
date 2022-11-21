package uk.gov.hmcts.dts.fact.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.CourtLock;
import uk.gov.hmcts.dts.fact.util.AdminFunctionalTestBase;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.hmcts.dts.fact.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
public class AdminCourtLockEndPointTest extends AdminFunctionalTestBase {

    private static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    private static final String COURT_LOCK_PATH = "/lock";
    private static final String PLYMOUTH_COMBINED_COURT_SLUG = "plymouth-combined-court";
    private static final String PLYMOUTH_COMBINED_COURT_LOCK_PATH = ADMIN_COURTS_ENDPOINT
        + PLYMOUTH_COMBINED_COURT_SLUG + COURT_LOCK_PATH;
    private static final String USER_EMAIL = "Test";
    private static final LocalDateTime TEST_LOCK_ACQUIRED =
        LocalDateTime.of(2001, 8, 28, 20, 20);


    /************************************************************* Get Request Tests. ***************************************************************/

    @Test
    public void returnLockForTheCourt() {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_LOCK_PATH,
            Map.of(AUTHORIZATION, BEARER + authenticatedToken)
        );
        assertThat(response.statusCode()).isEqualTo(OK.value());

//        final List<CourtLock> courtLocks = response.body().jsonPath().getList(".", CourtLock.class);
//        assertThat(courtLocks).isNotEmpty();
    }

    @Test
    public void shouldRequireATokenWhenGettingLocksForTheCourt() {
        final Response response = doGetRequest(PLYMOUTH_COMBINED_COURT_LOCK_PATH);
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    public void shouldBeForbiddenForGettingLocksForTheCourt() {
        final Response response = doGetRequest(
            PLYMOUTH_COMBINED_COURT_LOCK_PATH,
            Map.of(AUTHORIZATION, BEARER + forbiddenToken)
        );
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    /************************************************************* POST request tests section. ***************************************************************/

    @Test
    public void shouldCreateLock() throws JsonProcessingException {

        final List<CourtLock> currentCourtLock = getCurrentLock();
        final CourtLock expectedCourtLock = createCourtLock();
        final String newCourtLockJson = objectMapper().writeValueAsString(expectedCourtLock);

        final var response = doPostRequest(
            PLYMOUTH_COMBINED_COURT_LOCK_PATH,
            Map.of(AUTHORIZATION, BEARER + superAdminToken),
            newCourtLockJson
        );
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        final CourtLock createdCourtLock = response.as(CourtLock.class);
    }


        /************************************************************* Shared utility methods. ***************************************************************/

        private List<CourtLock> getCurrentLock () {
            final var response = doGetRequest(
                PLYMOUTH_COMBINED_COURT_LOCK_PATH,
                Map.of(AUTHORIZATION, BEARER + superAdminToken)
            );
            assertThat(response.statusCode()).isEqualTo(OK.value());
            return response.body().jsonPath().getList(".", CourtLock.class);
        }


        private CourtLock createCourtLock () {
            return new CourtLock(1, TEST_LOCK_ACQUIRED, PLYMOUTH_COMBINED_COURT_SLUG, USER_EMAIL);
        }


    }




//    private static final uk.gov.hmcts.dts.fact.entity.CourtLock ENTITY_COURT_LOCK_2 = new uk.gov.hmcts.dts.fact.entity.CourtLock(
//        2,
//        TEST_LOCK_ACQUIRED_2,
//        TEST_USER_2,
//        TEST_SLUG_2
//    );
