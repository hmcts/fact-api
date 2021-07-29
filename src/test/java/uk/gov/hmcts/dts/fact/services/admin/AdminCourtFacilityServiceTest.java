package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.*;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.FacilityTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtFacilityService.class)
public class AdminCourtFacilityServiceTest {

    private static final int FACILITY_COUNT = 3;
    private static final String COURT_SLUG = "some slug";
    private static final String FACILITY_TYPE_NAME = "name";
    private static final List<Facility> COURT_FACILITIES = new ArrayList<>();
    private static final String NOT_FOUND = "Not found: ";
    private static final String DUPLICATE_ERROR_MESSAGE = "Facility already exists: Facility3";
    private static final String FACILITY_1 = "Facility1";
    private static final String FACILITY_2 = "Facility2";
    private static final String FACILITY_3 = "Facility3";
    private static final String FACILITY_4 = "Facility4";
    private static final String DESCRIPTION_1 = "Description1";
    private static final String DESCRIPTION_CY_1 = "DescriptionCy1";
    private static final String DESCRIPTION_2 = "Description2";
    private static final String DESCRIPTION_CY_2 = "DescriptionCy2";
    private static final String DESCRIPTION_3 = "Description3";
    private static final String DESCRIPTION_CY_3 = "DescriptionCy3";
    private static final String DESCRIPTION_4 = "Description4";
    private static final String DESCRIPTION_CY_4 = "DescriptionCy4";
    private static final uk.gov.hmcts.dts.fact.model.admin.Facility FACILITY = new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_4,DESCRIPTION_4,DESCRIPTION_CY_4);

    @Autowired
    private AdminCourtFacilityService adminCourtFacilityService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private FacilityTypeRepository facilityTypeRepository;

    @Mock
    private Court court;

    @Mock
    private FacilityType facilityType;

    @BeforeEach
     void init() {

        final FacilityType facilityType1 = new FacilityType();
        facilityType1.setId(1);
        facilityType1.setName("FacilityType1");
        facilityType1.setOrder(1);
        final FacilityType facilityType2 = new FacilityType();
        facilityType2.setId(2);
        facilityType2.setName("FacilityType2");
        facilityType2.setOrder(2);
        final FacilityType facilityType3 = new FacilityType();
        facilityType3.setId(3);
        facilityType3.setName("FacilityType3");
        facilityType3.setOrder(3);

        final Facility facility1 = new Facility("Facility1","Description1","DescriptionCy1",facilityType1);
        final Facility facility2 = new Facility("Facility2","Description2","DescriptionCy2",facilityType2);
        final Facility facility3 = new Facility("Facility3","Description3","DescriptionCy3",facilityType1);

        COURT_FACILITIES.add(facility1);
        COURT_FACILITIES.add(facility2);
        COURT_FACILITIES.add(facility3);

    }

    @AfterEach
    void tearDown() {
        COURT_FACILITIES.clear();
    }

    @Test
    void shouldReturnCourtFacilities() {
        when(court.getFacilities()).thenReturn(COURT_FACILITIES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        assertThat(adminCourtFacilityService.getCourtFacilitiesBySlug(COURT_SLUG))
            .hasSize(FACILITY_COUNT)
            .first()
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.Facility.class);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingCourtFacilitiesForNonExistentCourt() {
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtFacilityService.getCourtFacilitiesBySlug(COURT_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldUpdateCourtFacility() {

        final List<uk.gov.hmcts.dts.fact.model.admin.Facility> expectedCourtFacilities = Arrays.asList(
            new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_1,DESCRIPTION_1,DESCRIPTION_CY_1),
            new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_2,DESCRIPTION_2,DESCRIPTION_CY_2),
            new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_3,"updated1","updated2"));

        final uk.gov.hmcts.dts.fact.model.admin.Facility updatedFacility = new uk.gov.hmcts.dts.fact.model.admin.Facility("Facility3","updated1","updated2");
        when(court.getFacilities()).thenReturn(COURT_FACILITIES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtRepository.save(court)).thenReturn(court);
        when(facilityTypeRepository.findByName(FACILITY_TYPE_NAME)).thenReturn(Optional.of(facilityType));

        assertThat(adminCourtFacilityService.updateCourtFacility(COURT_SLUG,updatedFacility))
            .hasSize(FACILITY_COUNT)
            .containsExactlyElementsOf(expectedCourtFacilities);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingCourtFacilityForNonExistentCourt() {

        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtFacilityService.updateCourtFacility(COURT_SLUG,FACILITY))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldAddNewCourtFacility() {

        final List<uk.gov.hmcts.dts.fact.model.admin.Facility> expectedCourtFacilities = Arrays.asList(
            new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_1,DESCRIPTION_1,DESCRIPTION_CY_1),
            new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_2,DESCRIPTION_2,DESCRIPTION_CY_2),
            new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_3,DESCRIPTION_3,DESCRIPTION_CY_3),
            new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_4,DESCRIPTION_4,DESCRIPTION_CY_4));

        when(court.getFacilities()).thenReturn(COURT_FACILITIES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtRepository.save(court)).thenReturn(court);
        when(facilityTypeRepository.findByName(FACILITY_TYPE_NAME)).thenReturn(Optional.of(facilityType));

        assertThat(adminCourtFacilityService.addCourtFacility(COURT_SLUG,FACILITY))
            .hasSize(4)
            .containsExactlyElementsOf(expectedCourtFacilities);
    }


    @Test
    void shouldDisplayMessageWhenFacilityAlreadyExists() {


        when(court.getFacilities()).thenReturn(COURT_FACILITIES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtRepository.save(court)).thenReturn(court);
        when(facilityTypeRepository.findByName(FACILITY_TYPE_NAME)).thenReturn(Optional.of(facilityType));

        assertThatThrownBy(() -> adminCourtFacilityService.addCourtFacility(COURT_SLUG, new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_3,DESCRIPTION_3,DESCRIPTION_CY_3)))
            .isInstanceOf(DuplicatedListItemException.class)
            .hasMessage(DUPLICATE_ERROR_MESSAGE);
    }

    @Test
    void shouldReturnNotFoundWhenAddingCourtFacilityForNonExistentCourt() {


        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtFacilityService.addCourtFacility(COURT_SLUG,FACILITY))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }

    @Test
    void shouldDeleteCourtFacility() {

        final List<uk.gov.hmcts.dts.fact.model.admin.Facility> expectedCourtFacilities = Arrays.asList(
            new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_1,DESCRIPTION_1,DESCRIPTION_CY_1),
            new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_2,DESCRIPTION_2,DESCRIPTION_CY_2));

        final uk.gov.hmcts.dts.fact.model.admin.Facility deletedFacility = new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_3,DESCRIPTION_3,DESCRIPTION_CY_3);

        when(court.getFacilities()).thenReturn(COURT_FACILITIES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtRepository.save(court)).thenReturn(court);
        when(facilityTypeRepository.findByName(FACILITY_TYPE_NAME)).thenReturn(Optional.of(facilityType));

        assertThat(adminCourtFacilityService.deleteCourtFacility(COURT_SLUG,deletedFacility))
            .hasSize(2)
            .containsExactlyElementsOf(expectedCourtFacilities);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingCourtFacilityForNonExistentCourt() {


        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtFacilityService.addCourtFacility(COURT_SLUG,FACILITY))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
    }
}

