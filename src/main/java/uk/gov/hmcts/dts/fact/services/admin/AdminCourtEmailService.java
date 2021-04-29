package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
import uk.gov.hmcts.dts.fact.repositories.CourtEmailRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.EmailTypeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class AdminCourtEmailService {

    private final CourtRepository courtRepository;
    private final CourtEmailRepository emailRepository;
    private final EmailTypeRepository emailTypeRepository;

    @Autowired
    public AdminCourtEmailService(final CourtRepository courtRepository,
                                  final CourtEmailRepository emailRepository,
                                  final EmailTypeRepository emailTypeRepository) {
        this.courtRepository = courtRepository;
        this.emailRepository = emailRepository;
        this.emailTypeRepository = emailTypeRepository;
    }

    public List<Email> getCourtEmailsBySlug(final String slug) {
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

    @Transactional(rollbackFor = {RuntimeException.class})
    public List<Email> updateEmailListForCourt(final String slug, final List<Email> emailList) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        List<uk.gov.hmcts.dts.fact.entity.Email> newEmailList = getNewEmails(emailList);
        List<CourtEmail> newCourtEmailList = getNewCourtEmails(courtEntity, newEmailList);

        // Remove existing emails and then replace with newly updated ones
        emailRepository.deleteAll(courtEntity.getCourtEmails());

        return emailRepository
            .saveAll(newCourtEmailList)
            .stream()
            .map(CourtEmail::getEmail)
            .collect(toList())
            .stream()
            .map(Email::new)
            .collect(toList());
    }

    private List<uk.gov.hmcts.dts.fact.entity.Email> getNewEmails(final List<Email> emailList) {
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.EmailType> emailTypeMap = getEmailTypeMap();
        return emailList.stream()
            .map(e -> new uk.gov.hmcts.dts.fact.entity.Email(e.getAddress(), e.getExplanation(),
                                                             e.getExplanationCy(),
                                                             emailTypeMap.get(e.getAdminEmailTypeId())
            )).collect(toList());
    }

    private Map<Integer, uk.gov.hmcts.dts.fact.entity.EmailType> getEmailTypeMap() {
        return emailTypeRepository.findAll()
            .stream()
            .collect(toMap(uk.gov.hmcts.dts.fact.entity.EmailType::getId, type -> type));
    }

    private List<CourtEmail> getNewCourtEmails(final Court court, final List<uk.gov.hmcts.dts.fact.entity.Email> emails) {
        final List<CourtEmail> newEmailsList = new ArrayList<>();
        for (int i = 0; i < emails.size(); i++) {
            newEmailsList.add(new CourtEmail(court, emails.get(i), i));
        }
        return newEmailsList;
    }

    public List<EmailType> getAllEmailTypes() {
        return emailTypeRepository.findAll()
            .stream()
            .map(EmailType::new)
            .collect(toList());
    }
}
