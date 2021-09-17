package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtDxCode;

import java.util.List;

public interface CourtDxCodesRepository extends JpaRepository<CourtDxCode, Integer> {
    List<CourtDxCode> findByCourtId(Integer courtId);
}
