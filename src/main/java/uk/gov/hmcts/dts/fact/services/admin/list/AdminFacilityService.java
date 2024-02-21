package uk.gov.hmcts.dts.fact.services.admin.list;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.ListItemInUseException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.FacilityType;
import uk.gov.hmcts.dts.fact.repositories.FacilityRepository;
import uk.gov.hmcts.dts.fact.repositories.FacilityTypeRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class AdminFacilityService {

    private final FacilityTypeRepository facilityTypeRepository;

    private final FacilityRepository facilityRepository;

    @Autowired
    public AdminFacilityService(
        final FacilityTypeRepository facilityTypeRepository, final FacilityRepository facilityRepository) {
        this.facilityTypeRepository = facilityTypeRepository;
        this.facilityRepository = facilityRepository;
    }

    public List<FacilityType> getAllFacilityTypes() {
        return facilityTypeRepository.findAll()
            .stream()
            .map(FacilityType::new)
            .collect(toList());
    }

    public FacilityType getFacilityType(Integer id) {
        return new FacilityType(facilityTypeRepository.findById(id).orElseThrow(() -> new NotFoundException(id.toString())));
    }

    public FacilityType createFacilityType(FacilityType facilityType) {
        checkIfFacilityTypeAlreadyExists(null, facilityType.getName());

        Optional<Integer> currentMaxOrder = getAllFacilityTypes()
            .stream()
            .map(FacilityType::getOrder)
            .max(Comparator.comparingInt(ord -> ord));

        Integer newOrder = currentMaxOrder.orElse(0) + 1;

        uk.gov.hmcts.dts.fact.entity.FacilityType newFacilityType = new uk.gov.hmcts.dts.fact.entity.FacilityType();
        newFacilityType.setName(facilityType.getName());
        newFacilityType.setNameCy(facilityType.getNameCy());
        newFacilityType.setOrder(newOrder);
        newFacilityType.setImage("");
        newFacilityType.setImageDescription("");

        return new FacilityType(facilityTypeRepository.save(newFacilityType));
    }

    public FacilityType updateFacilityType(FacilityType facilityType) {
        uk.gov.hmcts.dts.fact.entity.FacilityType entity = facilityTypeRepository.findById(facilityType.getId())
            .orElseThrow(() -> new NotFoundException(facilityType.getId().toString()));

        checkIfFacilityTypeAlreadyExists(entity.getName(), facilityType.getName());

        entity.setName(facilityType.getName());
        entity.setNameCy(facilityType.getNameCy());
        entity.setOrder(facilityType.getOrder());

        return new FacilityType(facilityTypeRepository.save(entity));
    }

    public void deleteFacilityType(Integer facilityTypeId) {
        FacilityType facilityType = getFacilityType(facilityTypeId);

        ensureFacilityTypeNotInUse(facilityType.getId());
        facilityTypeRepository.deleteById(facilityTypeId);
    }

    @Transactional
    public List<FacilityType> reorderFacilityTypes(List<Integer> idsInOrder) {
        for (int i = 0; i < idsInOrder.size(); i++) {
            Integer id = idsInOrder.get(i);

            @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
            uk.gov.hmcts.dts.fact.entity.FacilityType entity = facilityTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString()));

            entity.setOrder(i);

            facilityTypeRepository.save(entity);
        }

        return facilityTypeRepository.findAll().stream()
            .map(FacilityType::new)
            .collect(toList());
    }

    private void checkIfFacilityTypeAlreadyExists(String originalName, String newName) {
        List<FacilityType> facilityTypes = getAllFacilityTypes();
        if (originalName != null) {
            facilityTypes = facilityTypes.stream().filter(f -> !f.getName().equalsIgnoreCase(originalName)).collect(toList());
        }

        if (facilityTypes.stream().anyMatch(f -> f.getName().equalsIgnoreCase(newName))) {
            throw new DuplicatedListItemException("Facility already exists: " + newName);
        }
    }

    private void ensureFacilityTypeNotInUse(Integer id) {
        boolean inUse = facilityRepository
            .findAll()
            .stream()
            .anyMatch(f -> f.getFacilityType() != null && f.getFacilityType().getId().equals(id));

        if (inUse) {
            throw new ListItemInUseException(id.toString());
        }
    }
}
