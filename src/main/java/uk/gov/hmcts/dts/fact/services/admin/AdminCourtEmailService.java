package uk.gov.hmcts.dts.fact.services.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.EmailTypeRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class AdminCourtEmailService {

    private final CourtRepository courtRepository;
    private final EmailTypeRepository emailTypeRepository;

    @Autowired
    public AdminCourtEmailService(final CourtRepository courtRepository, final EmailTypeRepository emailTypeRepository) {
        this.courtRepository = courtRepository;
        this.emailTypeRepository = emailTypeRepository;
    }

    public List<Email> getCourtEmailsBySlug(final String slug) {

        log.info("does this go in here?");

        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtEmails()
                .stream()
                .map(CourtEmail::getEmail)
                .collect(toList())
                .stream()
                .map(Email::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    public List<EmailType> getAllEmailTypes() {
        return emailTypeRepository.findAll()
            .stream()
            .map(EmailType::new)
            .collect(toList());
    }
}
