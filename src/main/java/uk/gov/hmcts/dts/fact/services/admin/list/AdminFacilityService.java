package uk.gov.hmcts.dts.fact.services.admin.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.model.admin.FacilityType;
import uk.gov.hmcts.dts.fact.repositories.FacilityTypeRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminFacilityService {

    private final FacilityTypeRepository facilityTypeRepository;

    @Autowired
    public AdminFacilityService(final FacilityTypeRepository facilityTypeRepository) {
        this.facilityTypeRepository = facilityTypeRepository;
    }

    public List<FacilityType> getAllFacilityTypes() {
        return facilityTypeRepository.findAll()
            .stream()
            .map(FacilityType::new)
            .collect(toList());
    }

}
