package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import uk.gov.hmcts.dts.fact.entity.Audit;

public interface AuditRepository extends PagingAndSortingRepository<Audit, Integer> {
}
