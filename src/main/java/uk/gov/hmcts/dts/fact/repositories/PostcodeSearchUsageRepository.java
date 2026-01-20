package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.hmcts.dts.fact.entity.PostcodeSearchUsage;

public interface PostcodeSearchUsageRepository extends JpaRepository<PostcodeSearchUsage, Long> {
}
