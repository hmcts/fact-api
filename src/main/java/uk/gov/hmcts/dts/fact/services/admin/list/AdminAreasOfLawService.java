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
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.repositories.AreasOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtLocalAuthorityAreaOfLawRepository;
import uk.gov.hmcts.dts.fact.repositories.ServiceAreaRepository;
import uk.gov.hmcts.dts.fact.services.admin.AdminAuditService;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class AdminAreasOfLawService {

    private final AreasOfLawRepository areasOfLawRepository;
    private final AdminAuditService adminAuditService;
    private final CourtAreaOfLawRepository courtAreaOfLawRepository;
    private final CourtLocalAuthorityAreaOfLawRepository courtLocalAuthorityAreaOfLawRepo;
    private final ServiceAreaRepository serviceAreaRepository;

    @Autowired
    public AdminAreasOfLawService(
        final AreasOfLawRepository areasOfLawRepository,
        final CourtAreaOfLawRepository courtAreaOfLawRepository,
        final CourtLocalAuthorityAreaOfLawRepository courtLocalAuthorityAreaOfLawRepo,
        final ServiceAreaRepository serviceAreaRepository,
        final AdminAuditService adminAuditService) {

        this.areasOfLawRepository = areasOfLawRepository;
        this.courtAreaOfLawRepository = courtAreaOfLawRepository;
        this.courtLocalAuthorityAreaOfLawRepo = courtLocalAuthorityAreaOfLawRepo;
        this.serviceAreaRepository = serviceAreaRepository;
        this.adminAuditService = adminAuditService;
    }

    public AreaOfLaw getAreaOfLaw(final Integer id) {
        try {
            return new AreaOfLaw(areasOfLawRepository.getOne(id));
        } catch (final javax.persistence.EntityNotFoundException exception) {
            throw new NotFoundException(exception);
        }
    }

    public List<AreaOfLaw> getAllAreasOfLaw() {
        return areasOfLawRepository.findAll()
            .stream()
            .map(AreaOfLaw::new)
            .collect(toList());
    }

    @Transactional
    public AreaOfLaw updateAreaOfLaw(final AreaOfLaw updatedAreaOfLaw) {
        uk.gov.hmcts.dts.fact.entity.AreaOfLaw areaOfLawEntity =
            areasOfLawRepository.findById(updatedAreaOfLaw.getId())
            .orElseThrow(() -> new NotFoundException(updatedAreaOfLaw.getId().toString()));

        if (!updatedAreaOfLaw.getName().equalsIgnoreCase(updatedAreaOfLaw.getDisplayName())) {
            checkIfAreaOfLawAlreadyExists(updatedAreaOfLaw.getDisplayName());
        }
        else if (!updatedAreaOfLaw.getName().equalsIgnoreCase(updatedAreaOfLaw.getAlternativeName())) {
            checkIfAreaOfLawAlreadyExists(updatedAreaOfLaw.getAlternativeName());
        }

        final List<AreaOfLaw> originalAreasOfLaw = getAllAreasOfLaw();
        final uk.gov.hmcts.dts.fact.entity.AreaOfLaw entity = updateEntityPropertiesFromModel(updatedAreaOfLaw, areaOfLawEntity);
        AreaOfLaw newAreaOfLaw = new AreaOfLaw(areasOfLawRepository.save(entity));
        adminAuditService.saveAudit(AuditType.findByName("Update area of law"),
                                    originalAreasOfLaw,
                                    getAllAreasOfLaw(),
                                    null);
        return newAreaOfLaw;
    }

    @Transactional
    public AreaOfLaw createAreaOfLaw(final AreaOfLaw areaOfLaw) {
        checkIfAreaOfLawAlreadyExists(areaOfLaw.getName());
        AreaOfLaw newAreaOfLaw = new AreaOfLaw(areasOfLawRepository.save(createNewAreaOfLawEntityFromModel(areaOfLaw)));

        adminAuditService.saveAudit(AuditType.findByName("Create area of law"),
                                    areaOfLaw,
                                    newAreaOfLaw,
                                    null);
        return newAreaOfLaw;
    }

    public void deleteAreaOfLaw(final Integer areaOfLawId) {
        ensureAreaOfLawIsNotInUse(areaOfLawId);

        try {
            areasOfLawRepository.deleteById(areaOfLawId);
        } catch (EmptyResultDataAccessException ex) {
            log.warn("Area of Law could not be deleted because it no longer exists: " + areaOfLawId);
            throw new NotFoundException(ex);
        } catch (DataAccessException ex) {
            log.warn("A data access exception was thrown when trying to delete an area of law: " + areaOfLawId);
            throw new ListItemInUseException(ex);
        }
    }

    private void checkIfAreaOfLawAlreadyExists(final String areaOfLawName) {
        if (getAllAreasOfLaw().stream().anyMatch(aol -> aol.getName().equalsIgnoreCase(areaOfLawName))) {
            throw new DuplicatedListItemException("Area of Law already exists: " + areaOfLawName);
        }
    }

    private uk.gov.hmcts.dts.fact.entity.AreaOfLaw createNewAreaOfLawEntityFromModel(final AreaOfLaw areaOfLaw) {
        final uk.gov.hmcts.dts.fact.entity.AreaOfLaw entity = new uk.gov.hmcts.dts.fact.entity.AreaOfLaw();
        entity.setName(areaOfLaw.getName());
        return updateEntityPropertiesFromModel(areaOfLaw, entity);
    }

    private uk.gov.hmcts.dts.fact.entity.AreaOfLaw updateEntityPropertiesFromModel(final AreaOfLaw areaOfLaw,
                                                                                   final uk.gov.hmcts.dts.fact.entity.AreaOfLaw entity) {
        // Name is not updated because it is not editable.
        entity.setExternalLink(areaOfLaw.getExternalLink());
        entity.setExternalLinkCy(areaOfLaw.getExternalLink());
        entity.setExternalLinkDescription(areaOfLaw.getExternalLinkDescription());
        entity.setExternalLinkDescriptionCy(areaOfLaw.getExternalLinkDescriptionCy());
        entity.setAltName(areaOfLaw.getAlternativeName());
        entity.setAltNameCy(areaOfLaw.getAlternativeNameCy());
        entity.setDisplayName(areaOfLaw.getDisplayName());
        entity.setDisplayNameCy(areaOfLaw.getDisplayNameCy());
        entity.setDisplayExternalLink(areaOfLaw.getDisplayExternalLink());
        return entity;
    }

    private void ensureAreaOfLawIsNotInUse(Integer areaOfLawId) {
        if (!courtAreaOfLawRepository.getCourtAreaOfLawByAreaOfLawId(areaOfLawId).isEmpty()
            || !courtLocalAuthorityAreaOfLawRepo.findByAreaOfLawId(areaOfLawId).isEmpty()
            || !serviceAreaRepository.findByAreaOfLawId(areaOfLawId).isEmpty()) {
            throw new ListItemInUseException(areaOfLawId.toString());
        }
    }
}
