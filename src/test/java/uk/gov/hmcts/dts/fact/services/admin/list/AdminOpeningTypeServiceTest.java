package uk.gov.hmcts.dts.fact.services.admin.list;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.ListItemInUseException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.OpeningType;
import uk.gov.hmcts.dts.fact.repositories.OpeningTimeRepository;
import uk.gov.hmcts.dts.fact.repositories.OpeningTypeRepository;
import uk.gov.hmcts.dts.fact.services.admin.AdminAuditService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminOpeningTypeService.class)
class AdminOpeningTypeServiceTest {

    private static final List<uk.gov.hmcts.dts.fact.entity.OpeningType> OPENING_TYPES = Arrays.asList(
        new uk.gov.hmcts.dts.fact.entity.OpeningType(100, "Type1","type1Cy"),
        new uk.gov.hmcts.dts.fact.entity.OpeningType(200, "Type2","type2Cy"),
        new uk.gov.hmcts.dts.fact.entity.OpeningType(300, "Type3","type3Cy")
    );

    @Autowired
    private AdminOpeningTypeService adminOpeningTypeService;

    @MockBean
    private OpeningTimeRepository openingTimeRepository;

    @MockBean
    private OpeningTypeRepository openingTypeRepository;

    @MockBean
    private AdminAuditService adminAuditService;

    @Test
    void shouldReturnAllOpeningType() {

        when(openingTypeRepository.findAll()).thenReturn(OPENING_TYPES);

        final List<OpeningType> expectedResult = OPENING_TYPES
            .stream()
            .map(OpeningType::new)
            .collect(Collectors.toList());

        assertThat(adminOpeningTypeService.getAllOpeningTypes()).isEqualTo(expectedResult);
    }

    @Test
    void shouldReturnAOpeningTypeForGivenId() {
        final uk.gov.hmcts.dts.fact.entity.OpeningType mockOpeningType = OPENING_TYPES.get(1);
        when(openingTypeRepository.getReferenceById(100)).thenReturn(mockOpeningType);

        final OpeningType expectedResult =
            new OpeningType(mockOpeningType);

        assertThat(adminOpeningTypeService.getOpeningType(100)).isEqualTo(expectedResult);
    }

    @Test
    void whenIdDoesNotExistGetOpeningTypeShouldThrowNotFoundException() {
        when(openingTypeRepository.getReferenceById(400)).thenThrow(javax.persistence.EntityNotFoundException.class);
        assertThatThrownBy(() -> adminOpeningTypeService
            .getOpeningType(400))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldUpdateOpeningType() {
        final uk.gov.hmcts.dts.fact.entity.OpeningType mockOpeningType = OPENING_TYPES.get(1);
        final OpeningType openingType =
            new OpeningType(mockOpeningType);

        when(openingTypeRepository.findById(openingType.getId())).thenReturn(Optional.of(mockOpeningType));
        when(openingTypeRepository.save(mockOpeningType)).thenReturn(mockOpeningType);
        when(openingTypeRepository.getReferenceById(any())).thenReturn(OPENING_TYPES.get(1));

        assertThat(adminOpeningTypeService.updateOpeningType(openingType)).isEqualTo(openingType);

        verify(adminAuditService, atLeastOnce()).saveAudit("Update opening type",
                                                           openingType,
                                                           openingType, null);
    }

    @Test
    void updateShouldThrowNotFoundExceptionWhenOpeningTypeDoesNotExist() {
        OpeningType mockOpeningType = new OpeningType(OPENING_TYPES.get(1));
        when(openingTypeRepository.findById(mockOpeningType.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminOpeningTypeService
            .updateOpeningType(mockOpeningType))
            .isInstanceOf(NotFoundException.class);

        verify(openingTypeRepository, never()).save(any());
    }

    @Test
    void shouldCreateOpeningType() {
        final OpeningType newOpenType =
            new OpeningType();
        newOpenType.setType("New Open Type");
        newOpenType.setTypeCy("New Open Type Cy");

        when(openingTypeRepository.save(any(uk.gov.hmcts.dts.fact.entity.OpeningType.class)))
            .thenAnswer((Answer<uk.gov.hmcts.dts.fact.entity.OpeningType>) invocation -> invocation.getArgument(0));

        assertThat(adminOpeningTypeService.createOpeningType(newOpenType)).isEqualTo(newOpenType);

        verify(adminAuditService, atLeastOnce()).saveAudit("Create opening type",
                                                           newOpenType,
                                                           newOpenType, null);
    }

    @Test
    void createShouldThrowDuplicatedListItemExceptionIfOpeningTypeAlreadyExists() {
        final OpeningType newOpenType =
            new OpeningType();
        newOpenType.setId(100);
        newOpenType.setType("type1");
        newOpenType.setTypeCy("type1Cy");

        when(openingTypeRepository.findAll()).thenReturn(OPENING_TYPES);

        assertThatThrownBy(() -> adminOpeningTypeService
            .createOpeningType(newOpenType))
            .isInstanceOf(DuplicatedListItemException.class);
    }

    @Test
    void shouldDeleteContactType() {
        when(openingTimeRepository.getOpeningTimesByAdminTypeId(any()))
            .thenReturn(Collections.emptyList());
        when(openingTypeRepository.getReferenceById(any())).thenReturn(OPENING_TYPES.get(0));

        adminOpeningTypeService.deleteOpeningType(100);

        verify(openingTypeRepository).deleteById(100);

        verify(adminAuditService, atLeastOnce()).saveAudit("Delete opening type",
                                                           new OpeningType(100, "Type1","type1Cy"),
                                                           null, null);
    }

    @Test
    void deleteShouldThrowListItemInUseExceptionIfOpeningTypeInUse() {
        final DataAccessException mockDataAccessException = mock(DataAccessException.class);
        doThrow(mockDataAccessException).when(openingTypeRepository).deleteById(100);
        when(openingTypeRepository.getReferenceById(any())).thenReturn(OPENING_TYPES.get(0));

        assertThatThrownBy(() -> adminOpeningTypeService
            .deleteOpeningType(100))
            .isInstanceOf(ListItemInUseException.class);
    }


    @Test
    void deleteShouldThrowNotFoundExceptionIfIdDoesNotExists() {
        final EmptyResultDataAccessException mockEmptyResultException = mock(EmptyResultDataAccessException.class);
        doThrow(mockEmptyResultException).when(openingTypeRepository).deleteById(300);
        when(openingTypeRepository.getReferenceById(any())).thenReturn(OPENING_TYPES.get(0));

        assertThatThrownBy(() -> adminOpeningTypeService
            .deleteOpeningType(300))
            .isInstanceOf(NotFoundException.class);
    }


}
