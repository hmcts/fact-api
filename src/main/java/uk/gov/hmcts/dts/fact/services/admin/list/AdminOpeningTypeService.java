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
import uk.gov.hmcts.dts.fact.model.admin.OpeningType;
import uk.gov.hmcts.dts.fact.repositories.OpeningTimeRepository;
import uk.gov.hmcts.dts.fact.repositories.OpeningTypeRepository;
import uk.gov.hmcts.dts.fact.services.admin.AdminAuditService;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class AdminOpeningTypeService {

    private final OpeningTimeRepository openingTimeRepository;
    private final OpeningTypeRepository openingTypeRepository;
    private final AdminAuditService adminAuditService;

    @Autowired
    public AdminOpeningTypeService(final OpeningTimeRepository openingTimeRepository,
                                   final OpeningTypeRepository openingTypeRepository,
                                   final AdminAuditService adminAuditService) {
        this.openingTimeRepository = openingTimeRepository;
        this.openingTypeRepository = openingTypeRepository;
        this.adminAuditService = adminAuditService;
    }

    public List<OpeningType> getAllOpeningTypes() {
        return openingTypeRepository.findAll()
            .stream()
            .map(OpeningType::new)
            .sorted(Comparator.comparing(OpeningType::getType))
            .collect(toList());
    }


    public OpeningType getOpeningType(final Integer id) {
        try {
            return new OpeningType(openingTypeRepository.getReferenceById(id));
        } catch (final javax.persistence.EntityNotFoundException exception) {
            throw new NotFoundException(exception);
        }
    }

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

    private void checkIfOpeningTypeAlreadyExists(final String openingTypeName) {
        if (getAllOpeningTypes().stream().anyMatch(ct -> ct.getType().equalsIgnoreCase(openingTypeName))) {
            throw new DuplicatedListItemException("Opening Type already exists: " + openingTypeName);
        }
    }

    private uk.gov.hmcts.dts.fact.entity.OpeningType createNewOpeningTypeEntityFromModel(final OpeningType openingType) {
        final uk.gov.hmcts.dts.fact.entity.OpeningType entity = new uk.gov.hmcts.dts.fact.entity.OpeningType();
        entity.setDescription(openingType.getType());
        entity.setDescriptionCy(openingType.getTypeCy());
        return entity;
    }

    private void checkIfUpdatedOpeningTypeAlreadyExists(final OpeningType updatedOpeningType) {

        final List<OpeningType>  contactTypes =  getAllOpeningTypes();
        contactTypes.remove(updatedOpeningType);

        if (contactTypes.stream().anyMatch(ct -> ct.getType().equalsIgnoreCase(updatedOpeningType.getType()))) {
            throw new DuplicatedListItemException("Updated Opening Type already exists: " + updatedOpeningType.getType());
        }
    }

    private void checkOpeningTypeIsNotInUse(Integer contactTypeId) {
        if (!openingTimeRepository.getOpeningTimesByAdminTypeId(contactTypeId).isEmpty()) {
            throw new ListItemInUseException(contactTypeId.toString());
        }
    }
}
