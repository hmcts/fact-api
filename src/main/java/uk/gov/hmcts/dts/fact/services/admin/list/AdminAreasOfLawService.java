package uk.gov.hmcts.dts.fact.services.admin.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw;
import uk.gov.hmcts.dts.fact.repositories.AreasOfLawRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminAreasOfLawService {

    private final AreasOfLawRepository areasOfLawRepository;

    @Autowired
    public AdminAreasOfLawService(final AreasOfLawRepository areasOfLawRepository) {
        this.areasOfLawRepository = areasOfLawRepository;
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
        entity.setExternalLinkDescription(areaOfLaw.getExternalLinkDescription());
        entity.setExternalLinkDescriptionCy(areaOfLaw.getExternalLinkDescriptionCy());
        entity.setAltName(areaOfLaw.getAlternativeName());
        entity.setAltNameCy(areaOfLaw.getAlternativeNameCy());
        entity.setDisplayName(areaOfLaw.getDisplayName());
        entity.setDisplayNameCy(areaOfLaw.getDisplayNameCy());
        entity.setDisplayExternalLink(areaOfLaw.getDisplayExternalLink());
        return entity;
    }
}
