package uk.gov.hmcts.dts.fact.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.config.security.RolesProvider;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.CourtReference;
import uk.gov.hmcts.dts.fact.model.admin.CourtGeneral;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

@Service
public class AdminService {

    private final CourtRepository courtRepository;

    private final RolesProvider rolesProvider;

    @Autowired
    public AdminService(final CourtRepository courtRepository, final RolesProvider rolesProvider) {
        this.courtRepository = courtRepository;
        this.rolesProvider = rolesProvider;
    }

    public List<CourtReference> getAllCourts() {
        return courtRepository
            .findAll()
            .stream()
            .map(CourtReference::new)
            .collect(toList());
    }

    public CourtGeneral getCourtGeneralBySlug(String slug) {
        return courtRepository
            .findBySlug(slug)
            .map(CourtGeneral::new)
            .orElseThrow(() -> new NotFoundException(slug));
    }

    public Court getCourtEntityBySlug(String slug) {
        return courtRepository.findBySlug(slug).orElseThrow(() -> new NotFoundException(slug));
    }

    public CourtGeneral saveGeneral(String slug, CourtGeneral courtGeneral) {
        Court court = getCourtEntityBySlug(slug);
        court.setAlert(courtGeneral.getAlert());
        court.setAlertCy(courtGeneral.getAlertCy());
        if (rolesProvider.getRoles().contains("fact-super-admin")) {
            court.setInfo(courtGeneral.getInfo());
            court.setInfoCy(courtGeneral.getInfoCy());
        }
        Court updatedCourt = courtRepository.save(court);
        return new CourtGeneral(updatedCourt);
    }

    public uk.gov.hmcts.dts.fact.model.Court save(String slug, JsonNode update) {
        Court court = getCourtEntityBySlug(slug);

        overwriteIfSet(update, "name", court::setName);
        overwriteIfSet(update, "nameCy", court::setNameCy);
        overwriteIfSet(update, "slug", court::setSlug);
        overwriteBooleanIfSet(update, "displayed", court::setDisplayed);
        overwriteIfSet(update, "directions", court::setDirections);
        overwriteIfSet(update, "directionsCy", court::setDirectionsCy);
        overwriteDoubleIfSet(update, "lat", court::setLat);
        overwriteDoubleIfSet(update, "lon", court::setLon);
        overwriteIfSet(update, "alert", court::setAlert);
        overwriteIfSet(update, "alertCy", court::setAlertCy);
        overwriteIntIfSet(update, "number", court::setNumber);
        overwriteIntIfSet(update, "cciCode", court::setCciCode);
        overwriteIntIfSet(update, "magistrateCode", court::setMagistrateCode);
        overwriteBooleanIfSet(update, "hideAols", court::setHideAols);

        // TODO
        // aol
        // dx number
        // emails
        // in person
        // open
        // facilities
        // gbs
        // service area
        // types
        // contacts
        // opening times
        // addresses

        if (rolesProvider.getRoles().contains("fact-super-admin")) {
            overwriteIfSet(update, "info", court::setInfo);
            overwriteIfSet(update, "infoCy", court::setInfoCy);
        }

        court.setUpdatedAt(Timestamp.from(Instant.now()));
        Court updatedCourt = courtRepository.save(court);
        return new uk.gov.hmcts.dts.fact.model.Court(updatedCourt);
    }

    private void overwriteIfSet(JsonNode json, String fieldName, Consumer<String> setter) {
        if (json.has(fieldName)) {
            setter.accept(json.get(fieldName).asText());
        }
    }

    private void overwriteBooleanIfSet(JsonNode json, String fieldName, Consumer<Boolean> setter) {
        if (json.has(fieldName)) {
            setter.accept(json.get(fieldName).asBoolean());
        }
    }

    private void overwriteDoubleIfSet(JsonNode json, String fieldName, Consumer<Double> setter) {
        if (json.has(fieldName)) {
            setter.accept(json.get(fieldName).asDouble());
        }
    }

    private void overwriteIntIfSet(JsonNode json, String fieldName, Consumer<Integer> setter) {
        if (json.has(fieldName)) {
            setter.accept(json.get(fieldName).asInt());
        }
    }

}
