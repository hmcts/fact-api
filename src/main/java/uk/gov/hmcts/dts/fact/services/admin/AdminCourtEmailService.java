package uk.gov.hmcts.dts.fact.services.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtEmail;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.Email;
import uk.gov.hmcts.dts.fact.model.admin.EmailType;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.EmailRepository;
import uk.gov.hmcts.dts.fact.repositories.EmailTypeRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class AdminCourtEmailService {

    private final CourtRepository courtRepository;
    private final EmailRepository emailRepository;
    private final EmailTypeRepository emailTypeRepository;

    @Autowired
    public AdminCourtEmailService(final CourtRepository courtRepository,
                                  final EmailRepository emailRepository,
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

    public List<Email> updateEmailListForCourt(final String slug, final List<Email> emailList) {

        // search_court
        //  court_id (int)
        //      search_courtemail (table) - List<CourtEmail>)
        //          id
        //          court_id
        //          email_id
        //          order
        //              search_email (table) - List<Email>)

        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        log.info("Found a court, updating with info of {}", emailList.toArray());

        List<uk.gov.hmcts.dts.fact.entity.Email> newEmailList = getNewEmails(emailList);
        List<CourtEmail> newCourtEmailList = getNewCourtEmails(courtEntity, newEmailList);

        if (!CollectionUtils.isEmpty(courtEntity.getCourtOpeningTimes())) {
            log.info("deleted all court emails!");
            emailRepository.deleteAll();
        }

        List<CourtEmail> courtEmailList = emailRepository.saveAll(newCourtEmailList);
        log.info("Updated list is: {}", courtEmailList.toArray());

        return courtEmailList
            .stream()
            .map(CourtEmail::getEmail)
            .collect(toList()).stream()
            .map(Email::new)
            .collect(toList());
    }

    private List<uk.gov.hmcts.dts.fact.entity.Email> getNewEmails(final List<Email> emailList) {
        return emailList.stream()
            .map(e -> new uk.gov.hmcts.dts.fact.entity.Email(e.getAddress(), "", "",
                                                             e.getExplanation(), e.getExplanationCy(),
                                                             e.getAdminEmailTypeId()))
            .collect(toList());
    }

//    private Integer id;
//    private String address;
//    private String description;
//    private String descriptionCy;
//    private String explanation;
//    private String explanationCy;
//    private int adminEmailTypeId;

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
