package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.LocalAuthority;

import java.util.List;

public interface LocalAuthorityRepository extends JpaRepository<LocalAuthority, Integer> {
    List<LocalAuthority> findByName(String name);
}
