package uk.gov.hmcts.dts.fact.services.admin;

import com.launchdarkly.shaded.com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.Audit;
import uk.gov.hmcts.dts.fact.entity.AuditType;
import uk.gov.hmcts.dts.fact.repositories.AuditRepository;
import uk.gov.hmcts.dts.fact.repositories.AuditTypeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminAuditService.class)
//@SuppressWarnings("PMD.TooManyMethods")
public class AdminAuditServiceTest {

    @Autowired
    private AdminAuditService adminAuditService;

    @MockBean
    private AuditRepository auditRepository;

    @MockBean
    private AuditTypeRepository auditTypeRepository;

    private static final List<Audit> testData = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        testData.add(new Audit("Test String", new AuditType(1, "test"),
                               "data before", "data after",
                               LocalDateTime.of(1000, 10, 10, 10, 10)));
        testData.add(new Audit("Test String 2", new AuditType(1, "test 2"),
                               "data before 2", "data after 2",
                               LocalDateTime.of(1000, 10, 10, 10, 10)));
        testData.get(0).setId(0);
        testData.get(1).setId(1);
    }

    @Test
    void shouldGetAllAuditData() {
        when(auditRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(testData));

        final List<uk.gov.hmcts.dts.fact.model.admin.Audit> results =
            adminAuditService.getAllAuditData(0, 10);

        assertThat(results).hasSize(2);
        assertThat(results.get(0)).isEqualTo(new uk.gov.hmcts.dts.fact.model.admin.Audit(testData.get(0)));
        assertThat(results.get(1)).isEqualTo(new uk.gov.hmcts.dts.fact.model.admin.Audit(testData.get(1)));
    }

    @Test
    void shouldSaveAudit() {
        when(auditTypeRepository.findByName(anyString())).thenReturn(new AuditType(1, "test type"));
        when(auditRepository.save(any(Audit.class))).thenAnswer(i -> i.getArguments()[0]);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        adminAuditService.saveAudit("test type", testData, emptyList(), "some court");

        verify(auditRepository, atLeastOnce()).save(any(Audit.class));
    }
}
