package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtTypeRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminCourtTypesService {
    private final CourtRepository courtRepository;
    private final CourtTypeRepository courtTypeRepository;

    @Autowired
    public AdminCourtTypesService(final CourtRepository courtRepository, final CourtTypeRepository courtTypeRepository) {
        this.courtRepository = courtRepository;
        this.courtTypeRepository = courtTypeRepository;
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
        return mapCourtTypesCodes(returnCourtTypes,courtRepository.findBySlug(slug).get());
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

        //set court codes in Court Entity
        for (CourtType courtType : courtTypes) {


            switch (courtType.getName()) {
                case "Magistrates' Court":
                    courtEntity.setMagistrateCode(courtType.getCode());
                    break;
                case "County Court":
                    courtEntity.setCciCode(courtType.getCode());
                    break;
                case "Crown Court":
                    courtEntity.setNumber(courtType.getCode());
                    break;
                default:
                    break;
            }
        }

        final Court courtWithUpdatedCourtTypes = courtRepository.save(courtEntity);

        List<CourtType> returnCourtTypes = courtWithUpdatedCourtTypes.getCourtTypes()
            .stream()
            .map(CourtType::new)
            .collect(toList());


        return mapCourtTypesCodes(returnCourtTypes, courtWithUpdatedCourtTypes);
    }

    private List<uk.gov.hmcts.dts.fact.entity.CourtType> getNewCourtCourtTypesEntity(final List<CourtType> courtTypes) {
        return courtTypes.stream()
            .map(o -> new uk.gov.hmcts.dts.fact.entity.CourtType(o.getId(),o.getName()))
            .collect(toList());
    }

    protected List<CourtType> mapCourtTypesCodes(List<CourtType> courtTypes, Court court) {  //map court codes
        for (CourtType courtType : courtTypes) {

            switch (courtType.getName()) {
                case "Magistrates' Court":
                    courtType.setCode(court.getMagistrateCode());
                    break;
                case "County Court":
                    courtType.setCode(court.getCciCode());
                    break;
                case "Crown Court":
                    courtType.setCode(court.getNumber());
                    break;
                default:
                    break;
            }
        }

        return courtTypes;

    }


}
