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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static uk.gov.hmcts.dts.fact.util.Utils.NAME_IS_NOT_DX;

@Service
public class AdminCourtContactService {
    private final CourtRepository courtRepository;
    private final CourtContactRepository courtContactRepository;
    private final ContactTypeRepository contactTypeRepository;

    @Autowired
    public AdminCourtContactService(final CourtRepository courtRepository, final CourtContactRepository courtContactRepository, final ContactTypeRepository contactTypeRepository) {
        this.courtRepository = courtRepository;
        this.courtContactRepository = courtContactRepository;
        this.contactTypeRepository = contactTypeRepository;
    }

    public List<Contact> getCourtContactsBySlug(final String slug) {
        return courtRepository.findBySlug(slug)
            .map(c -> c.getCourtContacts()
                .stream()
                .map(CourtContact::getContact)
                .filter(NAME_IS_NOT_DX)
                .map(Contact::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
    }

    @Transactional()
    public List<Contact> updateCourtContacts(final String slug, final List<Contact> contacts) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        return saveNewCourtContacts(courtEntity, contacts);
    }

    public List<ContactType> getAllCourtContactTypes() {
        return contactTypeRepository.findAll()
            .stream()
            .map(ContactType::new)
            .collect(toList());
    }

    private List<Contact> saveNewCourtContacts(final Court courtEntity, final List<Contact> contacts) {
        final List<uk.gov.hmcts.dts.fact.entity.Contact> newContacts = getNewContacts(contacts);
        List<CourtContact> newCourtContacts = getNewCourtContacts(courtEntity, newContacts);

        final List<CourtContact> existingCourtContacts = courtEntity.getCourtContacts()
            .stream()
            .filter(c -> !c.getContact().getName().equalsIgnoreCase("DX"))
            .collect(toList());

        courtContactRepository.deleteAll(existingCourtContacts);
        return courtContactRepository.saveAll(newCourtContacts)
            .stream()
            .map(CourtContact::getContact)
            .filter(NAME_IS_NOT_DX)
            .map(Contact::new)
            .collect(toList());
    }

    @SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidInstantiatingObjectsInLoops"})
    private List<uk.gov.hmcts.dts.fact.entity.Contact> getNewContacts(final List<Contact> contacts) {
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.ContactType> contactTypeMap = getContactTypeMap();
        final List<uk.gov.hmcts.dts.fact.entity.Contact> newContacts = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            final Contact contact = contacts.get(i);
            newContacts.add(new uk.gov.hmcts.dts.fact.entity.Contact(contactTypeMap.get(contact.getTypeId()),
                                                                     contact.getNumber(),
                                                                     contact.getExplanation(),
                                                                     contact.getExplanationCy(),
                                                                     contact.isFax(),
                                                                     i));
        }
        return newContacts;
    }

    private Map<Integer, uk.gov.hmcts.dts.fact.entity.ContactType> getContactTypeMap() {
        return contactTypeRepository.findAll()
            .stream()
            .collect(toMap(uk.gov.hmcts.dts.fact.entity.ContactType::getId, type -> type));
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private List<CourtContact> getNewCourtContacts(final Court court, final List<uk.gov.hmcts.dts.fact.entity.Contact> contacts) {
        final List<CourtContact> courtContacts = new ArrayList<>();
        for (final uk.gov.hmcts.dts.fact.entity.Contact contact : contacts) {
            courtContacts.add(new CourtContact(court, contact));
        }
        return courtContacts;
    }
}
