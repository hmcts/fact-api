package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.OpeningTime;

import java.util.List;

public interface OpeningTimeRepository extends JpaRepository<OpeningTime, Integer> {
    List<OpeningTime> getOpeningTimesByAdminTypeId(Integer contactTypeId);
}
