package uk.gov.hmcts.dts.fact.services.admin.list;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.ListItemInUseException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.OpeningType;
import uk.gov.hmcts.dts.fact.repositories.OpeningTimeRepository;
import uk.gov.hmcts.dts.fact.repositories.OpeningTypeRepository;
import uk.gov.hmcts.dts.fact.services.admin.AdminAuditService;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Service for admin opening type data.
 */
@Service
@Slf4j
public class AdminOpeningTypeService {

    private final OpeningTimeRepository openingTimeRepository;
    private final OpeningTypeRepository openingTypeRepository;
    private final AdminAuditService adminAuditService;

    /**
     * Constructor for the AdminOpeningTypeService.
     * @param openingTimeRepository The repository for opening time
     * @param openingTypeRepository The repository for opening type
     * @param adminAuditService The service for admin audit
     */
    @Autowired
    public AdminOpeningTypeService(final OpeningTimeRepository openingTimeRepository,
                                   final OpeningTypeRepository openingTypeRepository,
                                   final AdminAuditService adminAuditService) {
        this.openingTimeRepository = openingTimeRepository;
        this.openingTypeRepository = openingTypeRepository;
        this.adminAuditService = adminAuditService;
    }

    /**
     * Get all opening types.
     * @return The opening types
     */
    public List<OpeningType> getAllOpeningTypes() {
        return openingTypeRepository.findAll()
            .stream()
            .map(OpeningType::new)
            .sorted(Comparator.comparing(OpeningType::getType))
            .collect(toList());
    }

    /**
     * Get an opening type by id.
     * @param id The id of the opening type
     * @return The opening type
     */
    public OpeningType getOpeningType(final Integer id) {
        try {
            return new OpeningType(openingTypeRepository.getReferenceById(id));
        } catch (final EntityNotFoundException exception) {
            throw new NotFoundException(exception);
        }
    }

    /**
     * Create a new opening type.
     * @param openingType The new opening type
     * @return The new opening type
     */
    @Transactional
    public OpeningType createOpeningType(final OpeningType openingType) {
        checkIfOpeningTypeAlreadyExists(openingType.getType());
        OpeningType newOpeningType  = new OpeningType(openingTypeRepository.save(createNewOpeningTypeEntityFromModel(openingType)));

        adminAuditService.saveAudit(
            AuditType.findByName("Create opening type"),
            openingType,
            newOpeningType,
            null);
        return newOpeningType;
    }

    /**
     * Update an opening type.
     * @param updatedOpeningType The updated opening type
     * @return The updated opening type
     */
    @Transactional
    public OpeningType updateOpeningType(final OpeningType updatedOpeningType) {
        uk.gov.hmcts.dts.fact.entity.OpeningType openingTypeEntity =
            openingTypeRepository.findById(updatedOpeningType.getId())
                .orElseThrow(() -> new NotFoundException(updatedOpeningType.getId().toString()));

        checkIfUpdatedOpeningTypeAlreadyExists(updatedOpeningType);
        openingTypeEntity.setDescription(updatedOpeningType.getType());
        openingTypeEntity.setDescriptionCy(updatedOpeningType.getTypeCy());

        OpeningType newOpeningType  =  new OpeningType(openingTypeRepository.save(openingTypeEntity));

        adminAuditService.saveAudit(
            AuditType.findByName("Update opening type"),
            getOpeningType(updatedOpeningType.getId()),
            newOpeningType,
            null);
        return newOpeningType;


    }

    /**
     * Delete an opening type.
     * @param openingTypeId The id of the opening type
     */
    public void deleteOpeningType(final Integer openingTypeId) {
        checkOpeningTypeIsNotInUse(openingTypeId);

        adminAuditService.saveAudit(
            AuditType.findByName("Delete opening type"),
            getOpeningType(openingTypeId),
            null,
            null);

        try {
            openingTypeRepository.deleteById(openingTypeId);
        } catch (EmptyResultDataAccessException ex) {
            log.warn("Opening Type could not be deleted because it no longer exists: " + openingTypeId);
            throw new NotFoundException(ex);
        } catch (DataAccessException ex) {
            log.warn("A data access exception was thrown when trying to delete a opening type: " + openingTypeId);
            throw new ListItemInUseException(ex);
        }
    }

    /**
     * Check if the opening type already exists.
     * @param openingTypeName The name of the opening type
     */
    private void checkIfOpeningTypeAlreadyExists(final String openingTypeName) {
        if (getAllOpeningTypes().stream().anyMatch(ct -> ct.getType().equalsIgnoreCase(openingTypeName))) {
            throw new DuplicatedListItemException("Opening Type already exists: " + openingTypeName);
        }
    }

    /**
     * Create a new opening type entity from a model.
     * @param openingType The opening type model
     * @return The new opening type entity
     */
    private uk.gov.hmcts.dts.fact.entity.OpeningType createNewOpeningTypeEntityFromModel(final OpeningType openingType) {
        final uk.gov.hmcts.dts.fact.entity.OpeningType entity = new uk.gov.hmcts.dts.fact.entity.OpeningType();
        entity.setDescription(openingType.getType());
        entity.setDescriptionCy(openingType.getTypeCy());
        return entity;
    }

    /**
     * Check if the updated opening type already exists.
     * @param updatedOpeningType The updated opening type
     */
    private void checkIfUpdatedOpeningTypeAlreadyExists(final OpeningType updatedOpeningType) {

        final List<OpeningType>  contactTypes =  getAllOpeningTypes();
        contactTypes.remove(updatedOpeningType);

        if (contactTypes.stream().anyMatch(ct -> ct.getType().equalsIgnoreCase(updatedOpeningType.getType()))) {
            throw new DuplicatedListItemException("Updated Opening Type already exists: " + updatedOpeningType.getType());
        }
    }

    /**
     * Check if the opening type is in use.
     * @param contactTypeId The id of the opening type
     */
    private void checkOpeningTypeIsNotInUse(Integer contactTypeId) {
        if (!openingTimeRepository.getOpeningTimesByAdminTypeId(contactTypeId).isEmpty()) {
            throw new ListItemInUseException(contactTypeId.toString());
        }
    }
}
