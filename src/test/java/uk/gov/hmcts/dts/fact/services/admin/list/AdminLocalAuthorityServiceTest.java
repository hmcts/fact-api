package uk.gov.hmcts.dts.fact.services.admin.list;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.LocalAuthority;
import uk.gov.hmcts.dts.fact.repositories.LocalAuthorityRepository;
import uk.gov.hmcts.dts.fact.services.admin.AdminAuditService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminLocalAuthorityService.class)
public class AdminLocalAuthorityServiceTest {

    @Autowired
    private AdminLocalAuthorityService localAuthorityService;

    @MockBean
    private LocalAuthorityRepository localAuthorityRepository;

    @MockBean
    private AdminAuditService adminAuditService;

    @Test
    void shouldReturnAllLocalAuthorities() {
        final List<uk.gov.hmcts.dts.fact.entity.LocalAuthority> mockLocalAuthorities = Arrays.asList(
            new uk.gov.hmcts.dts.fact.entity.LocalAuthority(100, "City of London Corporation"),
            new uk.gov.hmcts.dts.fact.entity.LocalAuthority(200, "Ealing Borough Council"),
            new uk.gov.hmcts.dts.fact.entity.LocalAuthority(300, "Greenwich Borough Council")
        );
        when(localAuthorityRepository.findAll()).thenReturn(mockLocalAuthorities);

        final List<LocalAuthority> expectedResult = mockLocalAuthorities
            .stream()
            .map(LocalAuthority::new)
            .collect(Collectors.toList());

        assertThat(localAuthorityService.getAllLocalAuthorities()).isEqualTo(expectedResult);
    }

    @Test
    void shouldUpdateLocalAuthority() {
        final LocalAuthority localAuthority = new LocalAuthority(900, "Test City Council");

        when(localAuthorityRepository.findById(localAuthority.getId()))
            .thenReturn(Optional.of(new uk.gov.hmcts.dts.fact.entity.LocalAuthority(localAuthority.getId(), localAuthority.getName())));
        when(localAuthorityRepository.save(any(uk.gov.hmcts.dts.fact.entity.LocalAuthority.class)))
            .thenAnswer((Answer<uk.gov.hmcts.dts.fact.entity.LocalAuthority>) invocation -> invocation.getArgument(0));

        assertThat(localAuthorityService.updateLocalAuthority(localAuthority.getId(), localAuthority.getName())).isEqualTo(localAuthority);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update local authority", emptyList().toString(),
                                                           emptyList().toString(), null);
    }

    @Test
    void updateShouldThrowNotFoundExceptionWhenLocalAuthorityDoesNotExist() {
        when(localAuthorityRepository.findById(900)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> localAuthorityService
            .updateLocalAuthority(900,"Test City Council"))
            .isInstanceOf(NotFoundException.class);

        verify(localAuthorityRepository, never()).save(any());
        verify(adminAuditService, never()).saveAudit(anyString(), any(), any(), any());
    }
}
