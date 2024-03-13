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

/**
 * Service for admin facility type data.
 */
@Service
@Slf4j
public class AdminFacilityService {

    private final FacilityTypeRepository facilityTypeRepository;

    private final FacilityRepository facilityRepository;

    /**
     * Constructor for the AdminFacilityService.
     * @param facilityTypeRepository The repository for facility type
     * @param facilityRepository The repository for facility
     */
    @Autowired
    public AdminFacilityService(
        final FacilityTypeRepository facilityTypeRepository, final FacilityRepository facilityRepository) {
        this.facilityTypeRepository = facilityTypeRepository;
        this.facilityRepository = facilityRepository;
    }

    /**
     * Get all facility types.
     * @return The facility types
     */
    public List<FacilityType> getAllFacilityTypes() {
        return facilityTypeRepository.findAll()
            .stream()
            .map(FacilityType::new)
            .collect(toList());
    }

    /**
     * Get a facility type by id.
     * @param id The id of the facility type
     * @return The facility type
     */
    public FacilityType getFacilityType(Integer id) {
        return new FacilityType(facilityTypeRepository.findById(id).orElseThrow(() -> new NotFoundException(id.toString())));
    }

    /**
     * Create a new facility type.
     * @param facilityType The new facility type
     * @return The new facility type
     */
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

    /**
     * Update a facility type.
     * @param facilityType The updated facility type
     * @return The updated facility type
     */
    public FacilityType updateFacilityType(FacilityType facilityType) {
        uk.gov.hmcts.dts.fact.entity.FacilityType entity = facilityTypeRepository.findById(facilityType.getId())
            .orElseThrow(() -> new NotFoundException(facilityType.getId().toString()));

        checkIfFacilityTypeAlreadyExists(entity.getName(), facilityType.getName());

        entity.setName(facilityType.getName());
        entity.setNameCy(facilityType.getNameCy());
        entity.setOrder(facilityType.getOrder());

        return new FacilityType(facilityTypeRepository.save(entity));
    }

    /**
     * Delete a facility type.
     * @param facilityTypeId The id of the facility type
     */
    public void deleteFacilityType(Integer facilityTypeId) {
        FacilityType facilityType = getFacilityType(facilityTypeId);

        ensureFacilityTypeNotInUse(facilityType.getId());
        facilityTypeRepository.deleteById(facilityTypeId);
    }

    /**
     * Reorder facility types.
     * @param idsInOrder The ids of the facility types in the new order
     * @return The reordered facility types
     */
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

    /**
     * Check if a facility type already exists.
     * @param originalName The original name of the facility type
     * @param newName The new name of the facility type
     */
    private void checkIfFacilityTypeAlreadyExists(String originalName, String newName) {
        List<FacilityType> facilityTypes = getAllFacilityTypes();
        if (originalName != null) {
            facilityTypes = facilityTypes.stream().filter(f -> !f.getName().equalsIgnoreCase(originalName)).collect(toList());
        }

        if (facilityTypes.stream().anyMatch(f -> f.getName().equalsIgnoreCase(newName))) {
            throw new DuplicatedListItemException("Facility already exists: " + newName);
        }
    }

    /**
     * Ensure a facility type is not in use.
     * @param id The id of the facility type
     */
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
