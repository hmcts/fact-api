package uk.gov.hmcts.dts.fact.services.admin.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtAreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.CourtLocalAuthorityAreaOfLaw;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.ListItemInUseException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.AreasOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtLocalAuthorityAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;
import uk.gov.hmcts.dts.fact.services.admin.AdminAuditService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports"})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminAreasOfLawService.class)
class AdminAreasOfLawServiceTest {

    @Autowired
    private AdminAreasOfLawService areasOfLawService;

    @MockBean
    private AreasOfLawRepository areasOfLawRepository;

    @MockBean
    private AdminAuditService adminAuditService;

    @MockBean
    private CourtAreaOfLawRepository courtAreaOfLawRepository;

    @MockBean
    private CourtLocalAuthorityAreaOfLawRepository courtLocalAuthorityAreaOfLawRepo;

    @MockBean
    private ServiceAreaRepository serviceAreaRepository;

    private static final List<AreaOfLaw> AREAS_OF_LAW = Arrays.asList(
        new AreaOfLaw(
            100,
            "Divorce",
            "https://divorce.test",
            null,
            "Information about getting a divorce",
            "Gwybodaeth ynglŷn â gwneud cais am ysgariad",
            "Divorce - alt",
            "Ysgariad alt",
            "Divorce - display",
            "Ysgariad display",
            "https://divorce.external.text"),
        new AreaOfLaw(
            200,
            "Tax",
            "https://tax.test",
            null,
            "Information about tax tribunals",
            "Gwybodaeth am tribiwnlysoedd treth",
            "Tax - alt",
            "Treth alt",
            "Tax - display",
            "Treth display",
            "https://tax.external.text"),
        new AreaOfLaw(
            300,
            "Employment",
            "https://employment.test",
            null,
            "Information about the Employment Tribunal",
            "Gwybodaeth ynglŷn â'r tribiwnlys cyflogaeth",
            "Employment - alt",
            "Honiadau yn erbyn cyflogwyr alt",
            "Employment - display",
            "Honiadau yn erbyn cyflogwyr display",
            "https://employment.external.text")
    );

    @Test
    void shouldReturnAllAreasOfLaw() {
        when(areasOfLawRepository.findAll()).thenReturn(AREAS_OF_LAW);

        final List<uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw> expectedResult = AREAS_OF_LAW
            .stream()
            .map(uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw::new)
            .collect(toList());

        assertThat(areasOfLawService.getAllAreasOfLaw()).isEqualTo(expectedResult);
    }

    @Test
    void shouldReturnAnAreaOfLawForGivenId() {
        final AreaOfLaw mockAreaOfLaw = AREAS_OF_LAW.get(0);
        when(areasOfLawRepository.getReferenceById(100)).thenReturn(mockAreaOfLaw);

        final uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw expectedResult =
            new uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw(mockAreaOfLaw);

        assertThat(areasOfLawService.getAreaOfLaw(100)).isEqualTo(expectedResult);
    }

    @Test
    void whenIdDoesNotExistGetAreaOfLawShouldThrowNotFoundException() {
        when(areasOfLawRepository.getReferenceById(400)).thenThrow(EntityNotFoundException.class);
        assertThatThrownBy(() -> areasOfLawService
            .getAreaOfLaw(400))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldUpdateAreaOfLaw() throws JsonProcessingException {
        final List<AreaOfLaw> areasOfLawCopy = AREAS_OF_LAW
            .stream()
            .map(aol -> new AreaOfLaw(
                aol.getId(), aol.getName(), aol.getExternalLink(), aol.getExternalLinkCy(),
                aol.getExternalLinkDescription(), aol.getExternalLinkDescriptionCy(),
                aol.getAltName(), aol.getAltNameCy(), aol.getDisplayName(), aol.getDisplayNameCy(),
                aol.getDisplayExternalLink()
            ))
            .collect(toList());
        final AreaOfLaw entity = AREAS_OF_LAW.get(0);
        final uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw areaOfLaw =
            new uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw(entity);
        areaOfLaw.setExternalLink("https://something.else.link");
        areaOfLaw.setExternalLinkDescription("A different description");
        areaOfLaw.setExternalLinkDescriptionCy("A different description in Welsh");

        when(areasOfLawRepository.findById(areaOfLaw.getId())).thenReturn(Optional.of(entity));
        when(areasOfLawRepository.save(entity)).thenReturn(entity);
        when(areasOfLawRepository.findAll()).thenReturn(AREAS_OF_LAW);

        assertThat(areasOfLawService.updateAreaOfLaw(areaOfLaw)).isEqualTo(areaOfLaw);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update area of law",
                                                           areasOfLawCopy.stream()
                                                               .map(uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw::new)
                                                               .collect(toList()),
                                                           AREAS_OF_LAW.stream()
                                                               .map(uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw::new)
                                                               .collect(toList()),
                                                           null);
    }

    @Test
    void updateShouldThrowNotFoundExceptionWhenAreaOfLawDoesNotExist() {
        final uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw testAreaOfLaw = new uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw(AREAS_OF_LAW.get(0));
        when(areasOfLawRepository.findById(testAreaOfLaw.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> areasOfLawService
            .updateAreaOfLaw(testAreaOfLaw))
            .isInstanceOf(NotFoundException.class);

        verify(areasOfLawRepository, never()).save(any());
        verify(adminAuditService, never()).saveAudit("Update area of law",
                                                     testAreaOfLaw,
                                                     testAreaOfLaw,
                                                     null);
    }

    @Test
    void shouldCreateAreaOfLaw() {
        final uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw areaOfLaw =
            new uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw();
        areaOfLaw.setName("New Area of Law");
        areaOfLaw.setExternalLink("https://newarea.of.law");
        areaOfLaw.setExternalLinkDescription("This is a new area of law");
        areaOfLaw.setExternalLinkDescriptionCy("This is a new area of law - welsh");
        areaOfLaw.setDisplayName("This is new area of law display name");
        areaOfLaw.setDisplayNameCy("This is new area of law display name - welsh");
        areaOfLaw.setDisplayExternalLink("https://external.newarea.of.law");

        when(areasOfLawRepository.save(any(AreaOfLaw.class)))
            .thenAnswer((Answer<AreaOfLaw>)invocation -> invocation.getArgument(0));

        assertThat(areasOfLawService.createAreaOfLaw(areaOfLaw)).isEqualTo(areaOfLaw);
        verify(adminAuditService, atLeastOnce()).saveAudit("Create area of law",
                                                           areaOfLaw,
                                                           areaOfLaw,
                                                           null);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @Test
    void createShouldThrowDuplicatedListItemExceptionIfAreaOfLawAlreadyExists() {
        when(areasOfLawRepository.findAll()).thenReturn(AREAS_OF_LAW);

        final uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw areaOfLaw =
            new uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw(AREAS_OF_LAW.get(0));

        assertThatThrownBy(() -> areasOfLawService
            .createAreaOfLaw(areaOfLaw))
            .isInstanceOf(DuplicatedListItemException.class);
        verify(adminAuditService, never()).saveAudit("Create area of law",
                                                     areaOfLaw,
                                                     areaOfLaw,
                                                     null);
    }

    @Test
    void shouldDeleteAreaOfLaw() {
        Integer areaOfLawId = 1;
        AreaOfLaw areaOfLaw = new AreaOfLaw(1, "test1");

        when(areasOfLawRepository.getReferenceById(areaOfLawId)).thenReturn(areaOfLaw);
        when(courtAreaOfLawRepository.getCourtAreaOfLawByAreaOfLawId(areaOfLawId)).thenReturn(Collections.emptyList());
        when(courtLocalAuthorityAreaOfLawRepo.findByAreaOfLawId(areaOfLawId)).thenReturn(Collections.emptyList());
        when(serviceAreaRepository.findByAreaOfLawId(areaOfLawId)).thenReturn(Collections.emptyList());

        areasOfLawService.deleteAreaOfLaw(areaOfLawId);
        verify(areasOfLawRepository, times(1)).deleteById(areaOfLawId);
    }

    @Test
    void deleteShouldThrowListItemInUseExceptionIfAreaOfLawIsUsedByCourt() {
        Integer areaOfLawId = 100;
        AreaOfLaw areaOfLaw = new AreaOfLaw(areaOfLawId, "test2");

        when(areasOfLawRepository.getReferenceById(areaOfLawId)).thenReturn(areaOfLaw);

        when(courtAreaOfLawRepository.getCourtAreaOfLawByAreaOfLawId(any()))
            .thenReturn(Arrays.asList(mock(CourtAreaOfLaw.class)));
        when(courtLocalAuthorityAreaOfLawRepo.findByAreaOfLawId(any()))
            .thenReturn(Collections.emptyList());
        when(serviceAreaRepository.findByAreaOfLawId(any()))
            .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> areasOfLawService
            .deleteAreaOfLaw(areaOfLawId))
            .isInstanceOf(ListItemInUseException.class);
    }

    @Test
    void deleteShouldThrowListItemInUseExceptionIfAreaOfLawIsUsedByCourtLocalAuthority() {
        Integer areaOfLawId = 100;
        AreaOfLaw areaOfLaw = new AreaOfLaw(areaOfLawId, "test3");

        when(areasOfLawRepository.getReferenceById(areaOfLawId)).thenReturn(areaOfLaw);

        when(courtLocalAuthorityAreaOfLawRepo.findByAreaOfLawId(any()))
            .thenReturn(Arrays.asList(mock(CourtLocalAuthorityAreaOfLaw.class)));
        when(courtAreaOfLawRepository.getCourtAreaOfLawByAreaOfLawId(any()))
            .thenReturn(Collections.emptyList());
        when(serviceAreaRepository.findByAreaOfLawId(any()))
            .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> areasOfLawService
            .deleteAreaOfLaw(areaOfLawId))
            .isInstanceOf(ListItemInUseException.class);
    }

    @Test
    void deleteShouldThrowListItemInUseExceptionIfAreaOfLawIsUsedByServiceArea() {
        Integer areaOfLawId = 100;
        AreaOfLaw areaOfLaw = new AreaOfLaw(areaOfLawId, "test4");

        when(areasOfLawRepository.getReferenceById(areaOfLawId)).thenReturn(areaOfLaw);

        when(serviceAreaRepository.findByAreaOfLawId(any()))
            .thenReturn(Arrays.asList(mock(ServiceArea.class)));
        when(courtLocalAuthorityAreaOfLawRepo.findByAreaOfLawId(any()))
            .thenReturn(Collections.emptyList());
        when(courtAreaOfLawRepository.getCourtAreaOfLawByAreaOfLawId(any()))
            .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> areasOfLawService
            .deleteAreaOfLaw(areaOfLawId))
            .isInstanceOf(ListItemInUseException.class);
    }

    @Test
    void deleteShouldThrowNotFoundExceptionIfIdDoesNotExists() {
        final EntityNotFoundException mockEntityNotFoundException = mock(EntityNotFoundException.class);
        doThrow(mockEntityNotFoundException).when(areasOfLawRepository).getReferenceById(300);

        assertThatThrownBy(() -> areasOfLawService
            .deleteAreaOfLaw(300))
            .isInstanceOf(NotFoundException.class);
    }
}
