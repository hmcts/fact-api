package uk.gov.hmcts.dts.fact.services.admin.list;

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

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminAreasOfLawService {

    private final AreasOfLawRepository areasOfLawRepository;
    private final CourtAreaOfLawRepository courtAreaOfLawRepository;
    private final CourtLocalAuthorityAreaOfLawRepository courtLocalAuthorityAreaOfLawRepo;
    private final ServiceAreaRepository serviceAreaRepository;

    @Autowired
    public AdminAreasOfLawService(
        final AreasOfLawRepository areasOfLawRepository,
        final CourtAreaOfLawRepository courtAreaOfLawRepository,
        final CourtLocalAuthorityAreaOfLawRepository courtLocalAuthorityAreaOfLawRepo,
        final ServiceAreaRepository serviceAreaRepository) {

        this.areasOfLawRepository = areasOfLawRepository;
        this.courtAreaOfLawRepository = courtAreaOfLawRepository;
        this.courtLocalAuthorityAreaOfLawRepo = courtLocalAuthorityAreaOfLawRepo;
        this.serviceAreaRepository = serviceAreaRepository;
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

        final uk.gov.hmcts.dts.fact.entity.AreaOfLaw entity = updateEntityPropertiesFromModel(updatedAreaOfLaw, areaOfLawEntity);
        return new AreaOfLaw(areasOfLawRepository.save(entity));
    }

    @Transactional
    public AreaOfLaw createAreaOfLaw(final AreaOfLaw areaOfLaw) {
        checkIfAreaOfLawAlreadyExists(areaOfLaw.getName());
        return new AreaOfLaw(areasOfLawRepository.save(createNewAreaOfLawEntityFromModel(areaOfLaw)));
    }

    public void deleteAreaOfLaw(final Integer areaOfLawId) {
        try {
            ensureAreaOfLawIsNotInUse(areaOfLawId);
            areasOfLawRepository.deleteById(areaOfLawId);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException(ex);
        } catch (DataAccessException ex) {
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
