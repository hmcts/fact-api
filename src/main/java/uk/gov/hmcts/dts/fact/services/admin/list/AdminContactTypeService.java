package uk.gov.hmcts.dts.fact.services.admin.list;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.ListItemInUseException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.ContactType;
import uk.gov.hmcts.dts.fact.repositories.ContactRepository;
import uk.gov.hmcts.dts.fact.repositories.ContactTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.EmailRepository;
import uk.gov.hmcts.dts.fact.repositories.EmailTypeRepository;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Service for admin contact type data.
 */
@Service
@Slf4j
public class AdminContactTypeService {

    private final ContactTypeRepository contactTypeRepository;
    private final ContactRepository contactRepository;
    private final EmailRepository emailRepository;
    private final EmailTypeRepository emailTypeRepository;

    /**
     * Constructor for the AdminContactTypeService.
     * @param contactTypeRepository The repository for contact type
     * @param contactRepository The repository for contact
     * @param emailRepository The repository for email
     * @param emailTypeRepository The repository for email type
     */
    @Autowired
    public AdminContactTypeService(final ContactTypeRepository contactTypeRepository,
                                   final ContactRepository contactRepository,
                                   final EmailRepository emailRepository,
                                   final EmailTypeRepository emailTypeRepository) {
        this.contactTypeRepository = contactTypeRepository;
        this.contactRepository = contactRepository;
        this.emailRepository = emailRepository;
        this.emailTypeRepository = emailTypeRepository;
    }

    /**
     * Get all contact types.
     * @return The contact types
     */
    public List<ContactType> getAllContactTypes() {
        return contactTypeRepository.findAll()
            .stream()
            .map(ContactType::new)
            .sorted(Comparator.comparing(ContactType::getType))
            .collect(toList());
    }

    /**
     * Get a contact type by id.
     * @param id The id of the contact type
     * @return The contact type
     */
    public ContactType getContactType(final Integer id) {
        try {
            return new ContactType(contactTypeRepository.getReferenceById(id));
        } catch (final jakarta.persistence.EntityNotFoundException exception) {
            throw new NotFoundException(exception);
        }
    }

    /**
     * Create a new contact type.
     * @param contactType The new contact type
     * @return The new contact type
     */
    @Transactional
    public ContactType createContactType(final ContactType contactType) {
        checkIfContactTypeAlreadyExists(contactType.getType());
        emailTypeRepository.save(new uk.gov.hmcts.dts.fact.entity.EmailType(contactType.getType(), contactType.getTypeCy()));
        return new ContactType(contactTypeRepository.save(createNewContactTypeEntityFromModel(contactType)));
    }

    /**
     * Update a contact type.
     * @param updatedContactType The updated contact type
     * @return The updated contact type
     */
    @Transactional
    public ContactType updateContactType(final ContactType updatedContactType) {
        uk.gov.hmcts.dts.fact.entity.ContactType contactTypeEntity =
            contactTypeRepository.findById(updatedContactType.getId())
                .orElseThrow(() -> new NotFoundException(updatedContactType.getId().toString()));
        checkIfUpdatedContactTypeAlreadyExists(updatedContactType);

        // Also get the email list type and update it too
        // Note: we are doing this as opposed to merging the two lists together to avoid having to
        // update each courts addresses in the live environment
        uk.gov.hmcts.dts.fact.entity.EmailType emailTypeEntity =
            emailTypeRepository.findByDescription(contactTypeEntity.getDescription())
                .orElseThrow(() -> new NotFoundException(updatedContactType.getType()));

        emailTypeEntity.setDescription(updatedContactType.getType());
        emailTypeEntity.setDescriptionCy(updatedContactType.getTypeCy());
        contactTypeEntity.setDescription(updatedContactType.getType());
        contactTypeEntity.setDescriptionCy(updatedContactType.getTypeCy());
        emailTypeRepository.save(emailTypeEntity);
        return new ContactType(contactTypeRepository.save(contactTypeEntity));
    }

    /**
     * Delete a contact type.
     * @param contactTypeId The id of the contact type
     */
    public void deleteContactType(final Integer contactTypeId) {

        uk.gov.hmcts.dts.fact.entity.ContactType contactTypeEntity =
            contactTypeRepository.findById(contactTypeId)
                .orElseThrow(() -> new NotFoundException(contactTypeId.toString()));
        checkContactTypeIsNotInUse(contactTypeId);
        uk.gov.hmcts.dts.fact.entity.EmailType emailTypeEntity =
            emailTypeRepository.findByDescription(contactTypeEntity.getDescription())
                .orElseThrow(() -> new NotFoundException(contactTypeEntity.getDescription()));
        checkEmailTypeIsNotInUse(emailTypeEntity.getId());

        try {
            emailTypeRepository.deleteById(emailTypeEntity.getId());
            contactTypeRepository.deleteById(contactTypeId);
        } catch (EmptyResultDataAccessException ex) {
            log.warn("Contact Type could not be deleted because it no longer exists: " + contactTypeId);
            throw new NotFoundException(ex);
        } catch (DataAccessException ex) {
            log.warn("A data access exception was thrown when trying to delete a contact type: " + contactTypeId);
            throw new ListItemInUseException(ex);
        }
    }

    /**
     * Check if the contact type already exists.
     * @param contactTypeName The name of the contact type
     */
    private void checkIfContactTypeAlreadyExists(final String contactTypeName) {
        if (getAllContactTypes().stream().anyMatch(ct -> ct.getType().equalsIgnoreCase(contactTypeName))) {
            throw new DuplicatedListItemException("Contact Type already exists: " + contactTypeName);
        }
    }

    /**
     * Check if the updated contact type already exists.
     * @param updatedContactType The updated contact type
     */
    private void checkIfUpdatedContactTypeAlreadyExists(final ContactType updatedContactType) {

        final List<ContactType>  contactTypes =  getAllContactTypes();
        contactTypes.remove(updatedContactType);

        if (contactTypes.stream().anyMatch(ct -> ct.getType().equalsIgnoreCase(updatedContactType.getType()))) {
            throw new DuplicatedListItemException("Updated Contact Type already exists: " + updatedContactType.getType());
        }
    }

    /**
     * Create a new contact type entity from a model.
     * @param contactType The contact type
     * @return The new contact type entity
     */
    private uk.gov.hmcts.dts.fact.entity.ContactType createNewContactTypeEntityFromModel(final ContactType contactType) {
        final uk.gov.hmcts.dts.fact.entity.ContactType entity = new uk.gov.hmcts.dts.fact.entity.ContactType();
        entity.setDescription(contactType.getType());
        entity.setDescriptionCy(contactType.getTypeCy());
        return entity;
    }

    /**
     * Check if the contact type is in use.
     * @param contactTypeId The id of the contact type
     */
    private void checkContactTypeIsNotInUse(Integer contactTypeId) {
        if (!contactRepository.getContactsByAdminTypeId(contactTypeId).isEmpty()) {
            throw new ListItemInUseException(contactTypeId.toString());
        }
    }

    /**
     * Check if the email type is in use.
     * @param emailTypeId The id of the email type
     */
    private void checkEmailTypeIsNotInUse(Integer emailTypeId) {
        if (!emailRepository.getEmailsByAdminTypeId(emailTypeId).isEmpty()) {
            throw new ListItemInUseException(emailTypeId.toString());
        }
    }
}



