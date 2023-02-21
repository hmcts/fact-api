package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.model.admin.Region;
import uk.gov.hmcts.dts.fact.repositories.RegionRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class AdminRegionService {
    private final RegionRepository regionRepository;

    public AdminRegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public List<Region> getAllRegions() {
        return regionRepository.findAll()
            .stream()
            .map(Region::new)
            .sorted(Comparator.comparing(Region::getName))
            .collect(toList());
    }

    public Map<Integer, uk.gov.hmcts.dts.fact.entity.Region> getRegionMap() {
        return regionRepository.findAll()
            .stream()
            .collect(toMap(uk.gov.hmcts.dts.fact.entity.Region::getId, region -> region));
    }
}
