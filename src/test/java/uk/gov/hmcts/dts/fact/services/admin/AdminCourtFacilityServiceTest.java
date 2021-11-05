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
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.CourtFacilityRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.repositories.FacilityTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtFacilityService.class)
public class AdminCourtFacilityServiceTest {

    private static final int FACILITY_COUNT = 3;
    private static final String COURT_SLUG = "some slug";
    private static final List<Facility> FACILITIES = new ArrayList<>();
    private static final List<CourtFacility> COURT_FACILITIES = new ArrayList<>();
    private static final String NOT_FOUND = "Not found: ";

    private static final int FACILITY_ID_1 = 1;
    private static final int FACILITY_ID_2 = 2;
    private static final int FACILITY_ID_3 = 3;
    private static final String DESCRIPTION_1 = "Description1";
    private static final String DESCRIPTION_CY_1 = "DescriptionCy1";
    private static final String DESCRIPTION_2 = "Description2";
    private static final String DESCRIPTION_CY_2 = "DescriptionCy2";
    private static final String DESCRIPTION_3 = "Description3";
    private static final String DESCRIPTION_CY_3 = "DescriptionCy3";

    private static final List<uk.gov.hmcts.dts.fact.model.admin.Facility> INPUT_COURT_FACILITIES = Arrays.asList(
        new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_ID_1,DESCRIPTION_1,DESCRIPTION_CY_1),
        new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_ID_2,DESCRIPTION_2,DESCRIPTION_CY_2),
        new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_ID_3,DESCRIPTION_3,DESCRIPTION_CY_3)
    );

    // The expected facility results are sorted based on the sort order of the facility types and not necessary the same as the input order
    private static final List<uk.gov.hmcts.dts.fact.model.admin.Facility> EXPECTED_COURT_FACILITIES = Arrays.asList(
        new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_ID_2,DESCRIPTION_2,DESCRIPTION_CY_2),
        new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_ID_3,DESCRIPTION_3,DESCRIPTION_CY_3),
        new uk.gov.hmcts.dts.fact.model.admin.Facility(FACILITY_ID_1,DESCRIPTION_1,DESCRIPTION_CY_1)
    );

    @Autowired
    private AdminCourtFacilityService adminCourtFacilityService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private FacilityTypeRepository facilityTypeRepository;

    @MockBean
    private CourtFacilityRepository courtFacilityRepository;

    @MockBean
    private AdminAuditService adminAuditService;

    @Mock
    private Court court;

    @Mock
    private FacilityType facilityType;

    @BeforeEach
    void init() {

        final FacilityType facilityType1 = new FacilityType();
        facilityType1.setId(1);
        facilityType1.setName("FacilityType1");
        facilityType1.setOrder(3);
        final FacilityType facilityType2 = new FacilityType();
        facilityType2.setId(2);
        facilityType2.setName("FacilityType2");
        facilityType2.setOrder(1);
        final FacilityType facilityType3 = new FacilityType();
        facilityType3.setId(3);
        facilityType3.setName("FacilityType3");
        facilityType3.setOrder(2);

        final Facility facility1 = new Facility("Description1", "DescriptionCy1", facilityType1);
        final Facility facility2 = new Facility("Description2", "DescriptionCy2", facilityType2);
        final Facility facility3 = new Facility("Description3", "DescriptionCy3", facilityType3);

        FACILITIES.add(facility1);
        FACILITIES.add(facility2);
        FACILITIES.add(facility3);

        final CourtFacility courtFacility1 = new CourtFacility(new Court(), facility1);
        final CourtFacility courtFacility2 = new CourtFacility(new Court(), facility2);
        final CourtFacility courtFacility3 = new CourtFacility(new Court(), facility3);

        COURT_FACILITIES.add(courtFacility1);
        COURT_FACILITIES.add(courtFacility2);
        COURT_FACILITIES.add(courtFacility3);
    }

    @AfterEach
    void tearDown() {
        FACILITIES.clear();
        COURT_FACILITIES.clear();
    }

    @Test
    void shouldReturnCourtFacilities() {
        when(court.getFacilities()).thenReturn(FACILITIES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));

        final List<uk.gov.hmcts.dts.fact.model.admin.Facility> results = adminCourtFacilityService.getCourtFacilitiesBySlug(COURT_SLUG);
        assertThat(results).hasSize(FACILITY_COUNT);
        assertThat(results).containsExactlyElementsOf(EXPECTED_COURT_FACILITIES);
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
        when(courtFacilityRepository.saveAll(any())).thenReturn(COURT_FACILITIES);
        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.of(court));
        when(courtFacilityRepository.findByCourtId(anyInt())).thenReturn(COURT_FACILITIES);
        when(courtRepository.save(court)).thenReturn(court);
        when(facilityTypeRepository.findById(any())).thenReturn(Optional.of(facilityType));

        List<uk.gov.hmcts.dts.fact.model.admin.Facility> results =
            adminCourtFacilityService.updateCourtFacility(COURT_SLUG, INPUT_COURT_FACILITIES);
        assertThat(results)
            .hasSize(FACILITY_COUNT)
            .containsExactlyElementsOf(EXPECTED_COURT_FACILITIES);
        verify(adminAuditService, atLeastOnce()).saveAudit("Update court facilities",
                                                           INPUT_COURT_FACILITIES,
                                                           results,
                                                           COURT_SLUG);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingCourtFacilityForNonExistentCourt() {

        when(courtRepository.findBySlug(COURT_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCourtFacilityService.updateCourtFacility(COURT_SLUG, INPUT_COURT_FACILITIES))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + COURT_SLUG);
        verify(adminAuditService, never()).saveAudit(anyString(), anyString(), anyString(), anyString());
    }
}

