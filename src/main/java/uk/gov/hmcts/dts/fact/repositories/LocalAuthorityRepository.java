package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.LocalAuthority;

public interface LocalAuthorityRepository extends JpaRepository<LocalAuthority, Integer> {
}
