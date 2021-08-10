package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.SidebarLocation;

import java.util.Optional;

public interface SidebarLocationRepository extends JpaRepository<SidebarLocation, Integer> {
    Optional<SidebarLocation> findSidebarLocationByName(String name);
}
