package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtContact;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.Contact;
import uk.gov.hmcts.dts.fact.model.admin.ContactType;
import uk.gov.hmcts.dts.fact.repositories.ContactTypeRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtContactRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.util.AuditType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Service for admin court contact data.
 */
@Service
public class AdminCourtContactService {
    private final CourtRepository courtRepository;
    private final CourtContactRepository courtContactRepository;
    private final ContactTypeRepository contactTypeRepository;
    private final AdminAuditService adminAuditService;

    /**
     * Constructor for the AdminCourtContactService.
     * @param courtRepository The repository for court
     * @param courtContactRepository The repository for court contact
     * @param contactTypeRepository The repository for contact type
     * @param adminAuditService The service for admin audit
     */
    @Autowired
    public AdminCourtContactService(final CourtRepository courtRepository,
                                    final CourtContactRepository courtContactRepository,
                                    final ContactTypeRepository contactTypeRepository,
                                    final AdminAuditService adminAuditService) {
        this.courtRepository = courtRepository;
        this.courtContactRepository = courtContactRepository;
        this.contactTypeRepository = contactTypeRepository;
        this.adminAuditService = adminAuditService;
    }

    /**
     * Get all court contacts by slug.
     * @param slug The slug
     * @return A list of contacts
     */
    public List<Contact> getCourtContactsBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtContacts()
                .stream()
                .map(CourtContact::getContact)
                .map(Contact::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    /**
     * Update court contacts.
     * @param slug The slug
     * @param contacts The contacts
     * @return A list of contacts
     */
    @Transactional()
    public List<Contact> updateCourtContacts(final String slug, final List<Contact> contacts) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        List<Contact> originalContactList = getCourtContactsBySlug(slug);
        List<Contact> newContactList = saveNewCourtContacts(courtEntity, contacts);
        adminAuditService.saveAudit(
            AuditType.findByName("Update court contacts"),
            originalContactList,
            newContactList, slug);
        return newContactList;
    }

    /**
     * Get all court contact types.
     * @return A list of contact types
     */
    public List<ContactType> getAllCourtContactTypes() {
        return contactTypeRepository.findAll()
            .stream()
            .map(ContactType::new)
            .sorted(Comparator.comparing(ContactType::getType))
            .collect(toList());
    }

    /**
     * Get a map of all court contact types.
     * @return A map of contact types
     */
    private List<Contact> saveNewCourtContacts(final Court courtEntity, final List<Contact> contacts) {
        final List<uk.gov.hmcts.dts.fact.entity.Contact> newContacts = getNewContacts(contacts);
        List<CourtContact> newCourtContacts = getNewCourtContacts(courtEntity, newContacts);

        final List<CourtContact> existingCourtContacts = new ArrayList<>(courtEntity.getCourtContacts());

        courtContactRepository.deleteAll(existingCourtContacts);
        return courtContactRepository.saveAll(newCourtContacts)
            .stream()
            .map(CourtContact::getContact)
            .map(Contact::new)
            .collect(toList());
    }

    /**
     * Construct new contacts.
     * @param contacts The contacts
     * @return A list of contacts
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private List<uk.gov.hmcts.dts.fact.entity.Contact> getNewContacts(final List<Contact> contacts) {
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.ContactType> contactTypeMap = getContactTypeMap();
        return contacts.stream()
            .map(contact -> new uk.gov.hmcts.dts.fact.entity.Contact(contactTypeMap.get(contact.getTypeId()),
                                                                     contact.getNumber(),
                                                                     contact.getExplanation(),
                                                                     contact.getExplanationCy(),
                                                                     contact.isFax()))
            .collect(toList());
    }

    /**
     * Get a map of all court contact types.
     * @return A map of contact types
     */
    private Map<Integer, uk.gov.hmcts.dts.fact.entity.ContactType> getContactTypeMap() {
        return contactTypeRepository.findAll()
            .stream()
            .collect(toMap(uk.gov.hmcts.dts.fact.entity.ContactType::getId, type -> type));
    }

    /**
     * Construct new court contacts.
     * @param court The court
     * @param contacts The contacts
     * @return A list of court contacts
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtContact> getNewCourtContacts(final Court court, final List<uk.gov.hmcts.dts.fact.entity.Contact> contacts) {
        final List<CourtContact> courtContacts = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            courtContacts.add(new CourtContact(court, contacts.get(i), i));
        }
        return courtContacts;
    }
}
