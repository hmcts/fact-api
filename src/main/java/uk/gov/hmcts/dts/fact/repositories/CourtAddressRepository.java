package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;

public interface CourtAddressRepository extends JpaRepository<CourtAddress, Integer> {
}
