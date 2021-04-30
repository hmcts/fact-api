package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtTypeRepository;
import uk.gov.hmcts.dts.fact.util.MapCourtCode;

import java.util.List;

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

    public List<CourtType> getCourtCourtTypesBySlug(final String slug) {
        List<CourtType> returnCourtTypes = courtRepository.findBySlug(slug)
            .map(c -> c.getCourtTypes()
                .stream()
                .map(CourtType::new)
                .collect(toList()))
            .orElseThrow(() -> new NotFoundException(slug));
        return mapCourtCode.mapCourtCodesForCourtTypeModel(returnCourtTypes, courtRepository.findBySlug(slug).get());
    }



    public List<CourtType> updateCourtCourtTypes(final String slug, final List<CourtType> courtTypes) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        return saveNewCourtCourtTypes(courtEntity, courtTypes);

    }


    protected List<CourtType> saveNewCourtCourtTypes(final Court courtEntity, final List<CourtType> courtTypes) {


        List<uk.gov.hmcts.dts.fact.entity.CourtType> courtTypeEntity = getNewCourtCourtTypesEntity(courtTypes);


        if (courtEntity.getCourtTypes() == null) {
            courtEntity.setCourtTypes(courtTypeEntity);
        } else {
            courtEntity.getCourtTypes().clear();
            courtEntity.getCourtTypes().addAll(courtTypeEntity);
        }

        Court amendedCourtEntity = mapCourtCode.mapCourtCodesForCourtEntity(courtTypes, courtEntity);

        final Court courtWithUpdatedCourtTypes = courtRepository.save(amendedCourtEntity);

        List<CourtType> returnCourtTypes = courtWithUpdatedCourtTypes.getCourtTypes()
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
