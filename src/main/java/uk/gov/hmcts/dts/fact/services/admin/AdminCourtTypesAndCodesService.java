package uk.gov.hmcts.dts.fact.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtDxCode;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.CourtType;
import uk.gov.hmcts.dts.fact.model.admin.CourtTypesAndCodes;
import uk.gov.hmcts.dts.fact.model.admin.DxCode;
import uk.gov.hmcts.dts.fact.repositories.CourtDxCodesRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtTypeRepository;
import uk.gov.hmcts.dts.fact.util.MapCourtCode;

import java.util.List;

import static java.util.stream.Collectors.toList;


@Service
public class AdminCourtTypesAndCodesService {
    private final CourtRepository courtRepository;
    private final CourtTypeRepository courtTypeRepository;
    private final MapCourtCode mapCourtCode;
    private final CourtDxCodesRepository courtDxCodesRepository;

    @Autowired
    public AdminCourtTypesAndCodesService(final CourtRepository courtRepository, final CourtTypeRepository courtTypeRepository,
                                          final MapCourtCode mapCourtCode, final CourtDxCodesRepository courtDxCodesRepository) {
        this.courtRepository = courtRepository;
        this.courtTypeRepository = courtTypeRepository;
        this.mapCourtCode = mapCourtCode;
        this.courtDxCodesRepository = courtDxCodesRepository;
    }

    public List<CourtType> getAllCourtTypes() {
        return  courtTypeRepository.findAll()
            .stream()
            .map(CourtType::new)
            .collect(toList());
    }

    public CourtTypesAndCodes getCourtTypesAndCodes(final String slug) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        return new CourtTypesAndCodes(getCourtCourtTypes(courtEntity), courtEntity.getGbs(), getCourtDxCodes(courtEntity));
    }

    private List<CourtType> getCourtCourtTypes(final Court courtEntity) {

        final List<CourtType> returnCourtTypes = courtEntity.getCourtTypes()
                .stream()
                .map(CourtType::new)
                .collect(toList());
        return mapCourtCode.mapCourtCodesForCourtTypeModel(returnCourtTypes, courtEntity);
    }

    public List<DxCode> getCourtDxCodes(final Court courtEntity) {
        return  courtDxCodesRepository.findByCourtId(courtEntity.getId())
            .stream()
            .map(CourtDxCode::getDxCode)
            .map(DxCode::new)
            .collect(toList());
    }

    public CourtTypesAndCodes updateCourtTypesAndCodes(final String slug, final CourtTypesAndCodes courtTypesAndCodes) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        return saveNewCourtTypesAndCodes(courtEntity, courtTypesAndCodes);

    }

    protected CourtTypesAndCodes saveNewCourtTypesAndCodes(final Court courtEntity, final CourtTypesAndCodes courtTypesAndCode) {

        final List<uk.gov.hmcts.dts.fact.entity.CourtType> courtTypeEntities = getNewCourtCourtTypesEntity(courtTypesAndCode.getCourtTypes());

        //save CourtTypesAndGbsCodes
        final Court amendedCourtEntity = saveCourtTypesAndGbsCodes(courtEntity,courtTypesAndCode,courtTypeEntities);

        saveCourtDxCodes(amendedCourtEntity, getNewDxCodeEntity(courtTypesAndCode.getDxCodes()));

        return new CourtTypesAndCodes(getCourtCourtTypes(amendedCourtEntity), amendedCourtEntity.getGbs(), getCourtDxCodes(amendedCourtEntity));
    }


    protected Court saveCourtTypesAndGbsCodes(final Court courtEntity, final CourtTypesAndCodes courtTypesAndCode, final List<uk.gov.hmcts.dts.fact.entity.CourtType> courtTypeEntity) {
        if (courtEntity.getCourtTypes() == null) {
            courtEntity.setCourtTypes(courtTypeEntity);
        } else {
            courtEntity.getCourtTypes().clear();
            courtEntity.getCourtTypes().addAll(courtTypeEntity);
        }

        //remove existing code entries for court as set by mapCourtCode utility.
        courtEntity.setMagistrateCode(null);
        courtEntity.setCciCode(null);
        courtEntity.setNumber(null);
        courtEntity.setCourtCode(null);
        courtEntity.setLocationCode(null);

        courtEntity.setGbs(courtTypesAndCode.getGbsCode());

        final Court amendedCourtEntity = mapCourtCode.mapCourtCodesForCourtEntity(courtTypesAndCode.getCourtTypes(), courtEntity);

        return courtRepository.save(amendedCourtEntity);
    }

    protected void saveCourtDxCodes(final Court courtEntity, final List<uk.gov.hmcts.dts.fact.entity.DxCode> dxCodeEntities) {
        final List<CourtDxCode> existingCourtDxCodes = courtDxCodesRepository.findByCourtId(courtEntity.getId());
        //remove existing court dx codes and save updated
        courtDxCodesRepository.deleteAll(existingCourtDxCodes);
        courtDxCodesRepository.saveAll(getNewCourtDxCodeEntity(courtEntity, dxCodeEntities));
    }

    private List<uk.gov.hmcts.dts.fact.entity.CourtType> getNewCourtCourtTypesEntity(final List<CourtType> courtTypes) {
        return courtTypes.stream()
            .map(o -> new uk.gov.hmcts.dts.fact.entity.CourtType(o.getId(),o.getName()))
            .collect(toList());
    }

    private List<uk.gov.hmcts.dts.fact.entity.DxCode> getNewDxCodeEntity(final List<DxCode> dxCodes) {
        return dxCodes.stream()
            .map(o -> new uk.gov.hmcts.dts.fact.entity.DxCode(o.getCode(),o.getExplanation(),o.getExplanationCy()))
            .collect(toList());
    }

    private List<CourtDxCode> getNewCourtDxCodeEntity(final Court court, final List<uk.gov.hmcts.dts.fact.entity.DxCode> dxCodes) {
        return dxCodes.stream()
            .map(o -> new CourtDxCode(court,o))
            .collect(toList());
    }
}
