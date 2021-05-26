package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.CourtType;

import java.util.Optional;


public interface CourtTypeRepository extends JpaRepository<CourtType, Integer> {

    @Override
    Optional<CourtType> findById(Integer id);

}
