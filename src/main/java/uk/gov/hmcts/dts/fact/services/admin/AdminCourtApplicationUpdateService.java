package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtApplicationUpdate;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.ApplicationUpdate;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class AdminCourtApplicationUpdateService {

    private final CourtRepository courtRepository;

    @Autowired
    public AdminCourtApplicationUpdateService(final CourtRepository courtRepository){
        this.courtRepository = courtRepository;
    }

    public List<ApplicationUpdate> getApplicationUpdatesBySlug(final String slug){
        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtApplicationUpdates()
                .stream()
                .map(CourtApplicationUpdate::getApplicationUpdate)
                .map(ApplicationUpdate::new)
                .collect(toList()))
                .orElseThrow(() -> new NotFoundException(slug));
    }

}
