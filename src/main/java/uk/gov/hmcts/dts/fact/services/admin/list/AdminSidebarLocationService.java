package uk.gov.hmcts.dts.fact.services.admin.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.model.admin.SidebarLocation;
import uk.gov.hmcts.dts.fact.repositories.SidebarLocationRepository;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class AdminSidebarLocationService {
    private final SidebarLocationRepository sidebarLocationRepository;

    @Autowired
    public AdminSidebarLocationService(final SidebarLocationRepository sidebarLocationRepository) {
        this.sidebarLocationRepository = sidebarLocationRepository;
    }

    public List<SidebarLocation> getAllSidebarLocations() {
        return sidebarLocationRepository.findAll()
            .stream()
            .map(SidebarLocation::new)
            .collect(toList());
    }

    public Map<Integer, uk.gov.hmcts.dts.fact.entity.SidebarLocation> getSidebarLocationMap() {
        return sidebarLocationRepository.findAll()
            .stream()
            .collect(toMap(uk.gov.hmcts.dts.fact.entity.SidebarLocation::getId, name -> name));
    }
}
