package uk.gov.hmcts.dts.fact.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.dts.fact.entity.Email;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Integer> {
    List<Email> getEmailsByAdminTypeId(Integer emailTypeId);
}
