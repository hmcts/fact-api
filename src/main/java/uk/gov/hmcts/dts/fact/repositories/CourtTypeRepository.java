package uk.gov.hmcts.dts.fact.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.dts.fact.entity.CourtType;

import java.util.List;
import java.util.Optional;

public interface CourtTypeRepository extends JpaRepository<CourtType, Integer> {

    Optional<CourtType> findByName(String name);
}
