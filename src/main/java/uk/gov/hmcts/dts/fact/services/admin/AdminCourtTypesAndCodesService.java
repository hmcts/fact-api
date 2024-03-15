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

/**
 * Service for admin court types and codes data.
 */
@Service
public class AdminCourtTypesAndCodesService {
    private final CourtRepository courtRepository;
    private final CourtTypeRepository courtTypeRepository;
    private final MapCourtCode mapCourtCode;
    private final CourtDxCodesRepository courtDxCodesRepository;

    /**
     * Constructor for the AdminCourtTypesAndCodesService.
     * @param courtRepository The repository for court
     * @param courtTypeRepository The repository for court type
     * @param mapCourtCode The utility for mapping court codes
     * @param courtDxCodesRepository The repository for court dx codes
     */
    @Autowired
    public AdminCourtTypesAndCodesService(final CourtRepository courtRepository, final CourtTypeRepository courtTypeRepository,
                                          final MapCourtCode mapCourtCode, final CourtDxCodesRepository courtDxCodesRepository) {
        this.courtRepository = courtRepository;
        this.courtTypeRepository = courtTypeRepository;
        this.mapCourtCode = mapCourtCode;
        this.courtDxCodesRepository = courtDxCodesRepository;
    }

    /**
     * Get all court types.
     * @return The court types
     */
    public List<CourtType> getAllCourtTypes() {
        return  courtTypeRepository.findAll()
            .stream()
            .map(CourtType::new)
            .collect(toList());
    }

    /**
     * Get the court types and codes for a court by slug.
     * @param slug The slug of the court
     * @return The court types and codes for the court
     */
    public CourtTypesAndCodes getCourtTypesAndCodes(final String slug) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));

        return new CourtTypesAndCodes(getCourtCourtTypes(courtEntity), courtEntity.getGbs(), getCourtDxCodes(courtEntity));
    }

    /**
     * Get Court types and codes for a court.
     * @param courtEntity The court entity
     * @return The updated court types and codes
     */
    private List<CourtType> getCourtCourtTypes(final Court courtEntity) {

        final List<CourtType> returnCourtTypes = courtEntity.getCourtTypes()
                .stream()
                .map(CourtType::new)
                .collect(toList());
        return mapCourtCode.mapCourtCodesForCourtTypeModel(returnCourtTypes, courtEntity);
    }

    /**
     * Get Court dx codes for a court.
     * @param courtEntity The court entity
     * @return The updated court dx codes
     */
    public List<DxCode> getCourtDxCodes(final Court courtEntity) {
        return  courtDxCodesRepository.findByCourtId(courtEntity.getId())
            .stream()
            .map(CourtDxCode::getDxCode)
            .map(DxCode::new)
            .collect(toList());
    }

    /**
     * Update the court types and codes for a court by slug.
     * @param slug The slug of the court
     * @param courtTypesAndCodes The new court types and codes
     * @return The updated court types and codes for the court
     */
    public CourtTypesAndCodes updateCourtTypesAndCodes(final String slug, final CourtTypesAndCodes courtTypesAndCodes) {
        final Court courtEntity = courtRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException(slug));
        return saveNewCourtTypesAndCodes(courtEntity, courtTypesAndCodes);

    }

    /**
     * Save new court types and codes for a court.
     * @param courtEntity The court entity
     * @param courtTypesAndCode The new court types and codes
     * @return The updated court types and codes for the court
     */
    protected CourtTypesAndCodes saveNewCourtTypesAndCodes(final Court courtEntity, final CourtTypesAndCodes courtTypesAndCode) {

        final List<uk.gov.hmcts.dts.fact.entity.CourtType> courtTypeEntities = getNewCourtCourtTypesEntity(courtTypesAndCode.getCourtTypes());

        //save CourtTypesAndGbsCodes
        final Court amendedCourtEntity = saveCourtTypesAndGbsCodes(courtEntity,courtTypesAndCode,courtTypeEntities);

        saveCourtDxCodes(amendedCourtEntity, getNewDxCodeEntity(courtTypesAndCode.getDxCodes()));

        return new CourtTypesAndCodes(getCourtCourtTypes(amendedCourtEntity), amendedCourtEntity.getGbs(), getCourtDxCodes(amendedCourtEntity));
    }

    /**
     * Save court types and gbs codes for a court.
     * @param courtEntity The court entity
     * @param courtTypesAndCode The new court types and codes
     * @param courtTypeEntity The new court type entity
     * @return The updated court types and codes for the court
     */
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

    /**
     * Save court dx codes for a court.
     * @param courtEntity The court entity
     * @param dxCodeEntities The new dx code entities
     */
    protected void saveCourtDxCodes(final Court courtEntity, final List<uk.gov.hmcts.dts.fact.entity.DxCode> dxCodeEntities) {
        final List<CourtDxCode> existingCourtDxCodes = courtDxCodesRepository.findByCourtId(courtEntity.getId());
        //remove existing court dx codes and save updated
        courtDxCodesRepository.deleteAll(existingCourtDxCodes);
        courtDxCodesRepository.saveAll(getNewCourtDxCodeEntity(courtEntity, dxCodeEntities));
    }

    /**
     * Get the new court types.
     * @param courtTypes The new court types
     * @return The new court types for the court
     */
    private List<uk.gov.hmcts.dts.fact.entity.CourtType> getNewCourtCourtTypesEntity(final List<CourtType> courtTypes) {
        return courtTypes.stream()
            .map(o -> new uk.gov.hmcts.dts.fact.entity.CourtType(o.getId(),o.getName(),o.getSearch()))
            .collect(toList());
    }

    /**
     * Get the new court dx codes for a court.
     * @param dxCodes The dx codes
     * @return The new court dx codes for the court
     */
    private List<uk.gov.hmcts.dts.fact.entity.DxCode> getNewDxCodeEntity(final List<DxCode> dxCodes) {
        return dxCodes.stream()
            .map(o -> new uk.gov.hmcts.dts.fact.entity.DxCode(o.getCode(),o.getExplanation(),o.getExplanationCy()))
            .collect(toList());
    }

    /**
     * Get the new court dx codes for a court.
     * @param court The court
     * @param dxCodes The dx codes
     * @return The new court dx codes for the court
     */
    private List<CourtDxCode> getNewCourtDxCodeEntity(final Court court, final List<uk.gov.hmcts.dts.fact.entity.DxCode> dxCodes) {
        return dxCodes.stream()
            .map(o -> new CourtDxCode(court,o))
            .collect(toList());
    }
}
