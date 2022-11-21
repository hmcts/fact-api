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
import uk.gov.hmcts.dts.fact.entity.Contact;
import uk.gov.hmcts.dts.fact.entity.Email;
import uk.gov.hmcts.dts.fact.entity.EmailType;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.ListItemInUseException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.ContactType;
import uk.gov.hmcts.dts.fact.repositories.ContactRepository;
import uk.gov.hmcts.dts.fact.repositories.ContactTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.EmailRepository;
import uk.gov.hmcts.dts.fact.repositories.EmailTypeRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminContactTypeService.class)
class AdminContactTypeServiceTest {

    private static final String TYPE_2 = "Type2";

    private static final List<uk.gov.hmcts.dts.fact.entity.ContactType> CONTACT_TYPES = Arrays.asList(
        new uk.gov.hmcts.dts.fact.entity.ContactType(100, "Type1", "type1Cy"),
        new uk.gov.hmcts.dts.fact.entity.ContactType(200, TYPE_2, "type2Cy"),
        new uk.gov.hmcts.dts.fact.entity.ContactType(300, "Type3", "type3Cy")
    );

    private static final List<EmailType> EMAIL_TYPES = Arrays.asList(
        new EmailType(1, "Type1", "type1Cy"),
        new EmailType(2, TYPE_2, "type2Cy"),
        new EmailType(3, "Type3", "type3Cy")
    );

    @Autowired
    private AdminContactTypeService adminContactTypeService;

    @MockBean
    private EmailRepository emailRepository;

    @MockBean
    private EmailTypeRepository emailTypeRepository;

    @MockBean
    private ContactTypeRepository contactTypeRepository;

    @MockBean
    private ContactRepository contactRepository;

    @Test
    void shouldReturnAllContactType() {

        when(contactTypeRepository.findAll()).thenReturn(CONTACT_TYPES);

        final List<ContactType> expectedResult = CONTACT_TYPES
            .stream()
            .map(ContactType::new)
            .collect(Collectors.toList());

        assertThat(adminContactTypeService.getAllContactTypes()).isEqualTo(expectedResult);
    }

    @Test
    void shouldReturnAContactTypeForGivenId() {
        final uk.gov.hmcts.dts.fact.entity.ContactType mockContactType = CONTACT_TYPES.get(1);
        when(contactTypeRepository.getOne(100)).thenReturn(mockContactType);

        final ContactType expectedResult =
            new ContactType(mockContactType);

        assertThat(adminContactTypeService.getContactType(100)).isEqualTo(expectedResult);
    }

    @Test
    void whenIdDoesNotExistGetContactTypeShouldThrowNotFoundException() {
        when(contactTypeRepository.getOne(400)).thenThrow(javax.persistence.EntityNotFoundException.class);
        assertThatThrownBy(() -> adminContactTypeService
            .getContactType(400))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldUpdateContactType() {
        final uk.gov.hmcts.dts.fact.entity.ContactType mockContactType = CONTACT_TYPES.get(1);
        final ContactType contactType =
            new ContactType(mockContactType);
        final EmailType mockEmailType = EMAIL_TYPES.get(1);
        final EmailType emailType = new EmailType();
        emailType.setId(mockEmailType.getId());
        emailType.setDescription(mockEmailType.getDescription());
        emailType.setDescriptionCy(mockEmailType.getDescriptionCy());

        when(emailTypeRepository.findByDescription(mockContactType.getDescription())).thenReturn(Optional.of(
            mockEmailType));
        when(emailTypeRepository.save(mockEmailType)).thenReturn(mockEmailType);
        when(contactTypeRepository.findById(contactType.getId())).thenReturn(Optional.of(mockContactType));
        when(contactTypeRepository.save(mockContactType)).thenReturn(mockContactType);
        assertThat(adminContactTypeService.updateContactType(contactType)).isEqualTo(contactType);

        verify(emailTypeRepository, times(1)).findByDescription(TYPE_2);
        verify(emailTypeRepository, times(1)).save(mockEmailType);
        verify(contactTypeRepository, times(1)).findById(200);
        verify(contactTypeRepository, times(1)).save(mockContactType);
    }

    @Test
    void updateShouldThrowNotFoundExceptionWhenContactTypeDoesNotExist() {
        final uk.gov.hmcts.dts.fact.entity.ContactType mockContactType = CONTACT_TYPES.get(1);
        when(contactTypeRepository.findById(mockContactType.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminContactTypeService
            .updateContactType(new ContactType(mockContactType)))
            .isInstanceOf(NotFoundException.class);

        verify(contactTypeRepository, never()).save(any());
    }

    @Test
    void shouldCreateContactType() {
        final ContactType newContactType = new ContactType();
        newContactType.setType("New Contact Type");
        newContactType.setTypeCy("New Contact Type Cy");

        when(contactTypeRepository.save(any(uk.gov.hmcts.dts.fact.entity.ContactType.class)))
            .thenAnswer((Answer<uk.gov.hmcts.dts.fact.entity.ContactType>) invocation -> invocation.getArgument(0));

        assertThat(adminContactTypeService.createContactType(newContactType)).isEqualTo(newContactType);
        verify(emailTypeRepository, times(1)).save(any(EmailType.class));
    }

    @Test
    void createShouldThrowDuplicatedListItemExceptionIfContactTypeAlreadyExists() {
        final ContactType newContactType =
            new ContactType();
        newContactType.setType("Type1");
        newContactType.setTypeCy("type1Cy");
        when(contactTypeRepository.findAll()).thenReturn(CONTACT_TYPES);

        assertThatThrownBy(() -> adminContactTypeService
            .createContactType(newContactType))
            .isInstanceOf(DuplicatedListItemException.class);
    }

    @Test
    void shouldDeleteContactType() {
        final uk.gov.hmcts.dts.fact.entity.ContactType mockContactType = CONTACT_TYPES.get(1);
        final EmailType mockEmailType = EMAIL_TYPES.get(1);
        final EmailType emailType = new EmailType();
        emailType.setId(mockEmailType.getId());
        emailType.setDescription(mockEmailType.getDescription());
        emailType.setDescriptionCy(mockEmailType.getDescriptionCy());

        when(emailRepository.getEmailsByAdminTypeId(any())).thenReturn(Collections.emptyList());
        when(contactRepository.getContactsByAdminTypeId(any()))
            .thenReturn(Collections.emptyList());
        when(emailTypeRepository.findByDescription(mockContactType.getDescription())).thenReturn(Optional.of(
            mockEmailType));
        when(contactTypeRepository.findById(123)).thenReturn(Optional.of(mockContactType));

        adminContactTypeService.deleteContactType(123);

        verify(emailTypeRepository, times(1)).findByDescription(TYPE_2);
        verify(emailTypeRepository, times(1)).deleteById(2);
        verify(contactTypeRepository, times(1)).deleteById(123);
    }

    @Test
    void deleteShouldThrowListItemInUseExceptionIfContactTypeInUse() {
        final uk.gov.hmcts.dts.fact.entity.ContactType mockContactType = CONTACT_TYPES.get(1);
        final EmailType mockEmailType = EMAIL_TYPES.get(1);
        when(contactRepository.getContactsByAdminTypeId(100))
            .thenReturn(List.of());
        when(emailTypeRepository.findByDescription(mockContactType.getDescription())).thenReturn(Optional.of(
            mockEmailType));
        when(contactTypeRepository.findById(100)).thenReturn(Optional.of(mockContactType));
        final DataAccessException mockDataAccessException = mock(DataAccessException.class);
        doThrow(mockDataAccessException).when(contactTypeRepository).deleteById(100);
        assertThatThrownBy(() -> adminContactTypeService
            .deleteContactType(100))
            .isInstanceOf(ListItemInUseException.class);
    }

    @Test
    void deleteShouldThrowListItemInUseExceptionIfContactTypeIsUsedByCourt() {
        final uk.gov.hmcts.dts.fact.entity.ContactType mockContactType = CONTACT_TYPES.get(1);
        final EmailType mockEmailType = EMAIL_TYPES.get(1);
        when(emailTypeRepository.findByDescription(mockContactType.getDescription())).thenReturn(Optional.of(
            mockEmailType));
        when(contactTypeRepository.findById(100)).thenReturn(Optional.of(mockContactType));
        when(contactRepository.getContactsByAdminTypeId(any()))
            .thenReturn(Arrays.asList(mock(Contact.class)));
        assertThatThrownBy(() -> adminContactTypeService
            .deleteContactType(100))
            .isInstanceOf(ListItemInUseException.class);
    }

    @Test
    void deleteShouldThrowListItemInUseExceptionIfEmailTypeIsUsedByCourt() {
        final uk.gov.hmcts.dts.fact.entity.ContactType mockContactType = CONTACT_TYPES.get(1);
        final EmailType mockEmailType = EMAIL_TYPES.get(1);
        when(emailTypeRepository.findByDescription(mockContactType.getDescription())).thenReturn(Optional.of(
            mockEmailType));
        when(contactTypeRepository.findById(100)).thenReturn(Optional.of(mockContactType));
        when(contactRepository.getContactsByAdminTypeId(any()))
            .thenReturn(Collections.emptyList());
        when(emailRepository.getEmailsByAdminTypeId(any()))
            .thenReturn(Arrays.asList(mock(Email.class)));
        assertThatThrownBy(() -> adminContactTypeService
            .deleteContactType(100))
            .isInstanceOf(ListItemInUseException.class);
    }

    @Test
    void deleteShouldThrowNotFoundExceptionIfIdDoesNotExists() {
        final EmptyResultDataAccessException mockEmptyResultException = mock(EmptyResultDataAccessException.class);
        doThrow(mockEmptyResultException).when(contactTypeRepository).deleteById(300);

        assertThatThrownBy(() -> adminContactTypeService
            .deleteContactType(300))
            .isInstanceOf(NotFoundException.class);
    }
}

