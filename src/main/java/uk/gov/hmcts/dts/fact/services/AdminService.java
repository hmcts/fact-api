package uk.gov.hmcts.dts.fact.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.AdminCourt;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminService {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    CourtRepository courtRepository;

    public List<CourtReference> getAllCourts() {
        return courtRepository
            .findAll()
            .stream()
            .map(CourtReference::new)
            .collect(toList());
    }

    public AdminCourt getCourtBySlug(String slug) {
        return courtRepository
            .findBySlug(slug)
            .map(AdminCourt::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    public Court getCourtEntityBySlug(String slug) {
        return courtRepository.findBySlug(slug).get();
    }

    public AdminCourt saveGeneral(String slug, CourtGeneral courtGeneral) {
        Court court = getCourtEntityBySlug(slug);
        court.setName(courtGeneral.getName());
        court.setNameCy(courtGeneral.getNameCy());
        court.setInfo(courtGeneral.getInfo());
        court.setInfoCy(courtGeneral.getInfoCy());
        court.setDisplayed(courtGeneral.getOpen());
        court.setAlert(courtGeneral.getAlert());
        court.setAlertCy(courtGeneral.getAlertCy());
        Court updatedCourt = courtRepository.save(court);
        return new AdminCourt(updatedCourt);
    }
}
