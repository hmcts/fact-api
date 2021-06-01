package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtTypeRepository;
import uk.gov.hmcts.dts.fact.util.MapCourtCode;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtTypesService {
    private final CourtRepository courtRepository;
    private final CourtTypeRepository courtTypeRepository;
    private final MapCourtCode mapCourtCode;

    @Autowired
    public AdminCourtTypesService(final CourtRepository courtRepository, final CourtTypeRepository courtTypeRepository, final MapCourtCode mapCourtCode) {
        this.courtRepository = courtRepository;
        this.courtTypeRepository = courtTypeRepository;
        this.mapCourtCode = mapCourtCode;
    }

    public List<CourtType> getAllCourtTypes() {
        return  courtTypeRepository.findAll()
            .stream()
            .map(CourtType::new)
            .collect(toList());
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<CourtType> getCourtCourtTypesBySlug(final String slug) {

        final Optional<Court> court = courtRepository.findBySlug(slug);

        final List<CourtType> returnCourtTypes = court
            .map(c -> c.getCourtTypes()
                .stream()
                .map(CourtType::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));

        if (court.isPresent()) {
            return mapCourtCode.mapCourtCodesForCourtTypeModel(returnCourtTypes, court.get());
        }
        return emptyList();
    }

    @Transactional()
    public List<CourtType> updateCourtCourtTypes(final String slug, final List<CourtType> courtTypes) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        return saveNewCourtCourtTypes(courtEntity, courtTypes);

    }

    protected List<CourtType> saveNewCourtCourtTypes(final Court courtEntity, final List<CourtType> courtTypes) {

        final List<uk.gov.hmcts.dts.fact.entity.CourtType> courtTypeEntity = getNewCourtCourtTypesEntity(courtTypes);

        if (courtEntity.getCourtTypes() == null) {
            courtEntity.setCourtTypes(courtTypeEntity);
        } else {
            courtEntity.getCourtTypes().clear();
            courtEntity.getCourtTypes().addAll(courtTypeEntity);
        }

        final Court amendedCourtEntity = mapCourtCode.mapCourtCodesForCourtEntity(courtTypes, courtEntity);

        final Court courtWithUpdatedCourtTypes = courtRepository.save(amendedCourtEntity);

        final List<CourtType> returnCourtTypes = courtWithUpdatedCourtTypes.getCourtTypes()
            .stream()
            .map(CourtType::new)
            .collect(toList());

        return mapCourtCode.mapCourtCodesForCourtTypeModel(returnCourtTypes, courtWithUpdatedCourtTypes);
    }

    private List<uk.gov.hmcts.dts.fact.entity.CourtType> getNewCourtCourtTypesEntity(final List<CourtType> courtTypes) {
        return courtTypes.stream()
            .map(o -> new uk.gov.hmcts.dts.fact.entity.CourtType(o.getId(),o.getName()))
            .collect(toList());
    }
}
