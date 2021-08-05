package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.SidebarLocation;

public interface SidebarLocationRepository extends JpaRepository<SidebarLocation, Integer> {
}
