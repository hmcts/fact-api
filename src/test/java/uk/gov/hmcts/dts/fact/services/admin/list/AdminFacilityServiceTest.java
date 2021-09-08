package uk.gov.hmcts.dts.fact.services.admin.list;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.Facility;
import uk.gov.hmcts.dts.fact.entity.FacilityType;
import uk.gov.hmcts.dts.fact.exception.DuplicatedListItemException;
import uk.gov.hmcts.dts.fact.exception.ListItemInUseException;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.repositories.FacilityRepository;
import uk.gov.hmcts.dts.fact.repositories.FacilityTypeRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminFacilityService.class)
public class AdminFacilityServiceTest {

    private static final int FACILITY_TYPE_COUNT = 3;

    @Autowired
    private AdminFacilityService adminFacilityService;

    @MockBean
    private FacilityTypeRepository facilityTypeRepository;

    @MockBean
    private FacilityRepository facilityRepository;

    @Test
    void shouldReturnAllFacilities() {

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

        final List<FacilityType> mockFacilites = new ArrayList<>();
        mockFacilites.add(facilityType1);
        mockFacilites.add(facilityType2);
        mockFacilites.add(facilityType3);

        when(facilityTypeRepository.findAll()).thenReturn(mockFacilites);

        assertThat(adminFacilityService.getAllFacilityTypes())
            .hasSize(FACILITY_TYPE_COUNT)
            .first()
            .isInstanceOf(uk.gov.hmcts.dts.fact.model.admin.FacilityType.class);
    }

    @Test
    void shouldUpdateFacilityType() {
        final Integer id = 100;
        final FacilityType entity = getFacilityType(id, "Lift", "Lifft");
        final uk.gov.hmcts.dts.fact.model.admin.FacilityType facilityType = new uk.gov.hmcts.dts.fact.model.admin.FacilityType(entity);

        when(facilityTypeRepository.findById(id)).thenReturn(Optional.of(entity));
        when(facilityTypeRepository.save(entity)).thenReturn(entity);

        assertThat(adminFacilityService.updateFacilityType(facilityType)).isEqualTo(facilityType);
    }

    @Test
    void updateShouldThrowNotFoundExceptionWhenFacilityTypeDoesNotExist() {
        final Integer id = 200;
        final uk.gov.hmcts.dts.fact.model.admin.FacilityType facilityType = new uk.gov.hmcts.dts.fact.model.admin.FacilityType();
        facilityType.setId(id);
        facilityType.setName("Parking");

        when(facilityTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminFacilityService
            .updateFacilityType(facilityType))
            .isInstanceOf(NotFoundException.class);

        verify(facilityTypeRepository, never()).save(any());
    }

    @Test
    void shouldCreateFacilityType() {
        final uk.gov.hmcts.dts.fact.model.admin.FacilityType facilityType = new uk.gov.hmcts.dts.fact.model.admin.FacilityType();
        facilityType.setName("Security");
        facilityType.setNameCy("Security (Welsh)");
        facilityType.setOrder(1);
        facilityType.setImage("");
        facilityType.setImageDescription("");

        when(facilityTypeRepository.save(any(FacilityType.class)))
            .thenAnswer((Answer<FacilityType>) invocation -> invocation.getArgument(0));

        assertThat(adminFacilityService.createFacilityType(facilityType)).isEqualTo(facilityType);
    }

    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    void createShouldThrowDuplicatedListItemExceptionIfFacilityTypeAlreadyExists() {
        final List<FacilityType> allFacilityTypes = Arrays.asList(getFacilityType(900, "Parking", "Parcio"));

        when(facilityTypeRepository.findAll()).thenReturn(allFacilityTypes);

        final uk.gov.hmcts.dts.fact.model.admin.FacilityType facilityType =
            new uk.gov.hmcts.dts.fact.model.admin.FacilityType(allFacilityTypes.get(0));

        assertThatThrownBy(() -> adminFacilityService
            .createFacilityType(facilityType))
            .isInstanceOf(DuplicatedListItemException.class);
    }

    @Test
    void shouldDeleteFacilityType() {
        when(facilityRepository.findAll()).thenReturn(Collections.emptyList());

        adminFacilityService.deleteFacilityType(123);

        verify(facilityTypeRepository).deleteById(123);
    }

    @Test
    void deleteShouldThrowNotFoundExceptionIfFacilityTypeDoesNotExist() {
        final Integer id = 321;
        final EmptyResultDataAccessException mockEmptyResultException = mock(EmptyResultDataAccessException.class);
        doThrow(mockEmptyResultException).when(facilityTypeRepository).deleteById(id);

        assertThatThrownBy(() -> adminFacilityService
            .deleteFacilityType(id))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteShouldThrowListItemInUseExceptionIfFacilityTypeInUse() {
        // Mock a foreign key constraint violation
        final Integer id = 345;
        final DataAccessException mockDataAccessException = mock(DataAccessException.class);
        doThrow(mockDataAccessException).when(facilityTypeRepository).deleteById(id);
        assertThatThrownBy(() -> adminFacilityService
            .deleteFacilityType(id))
            .isInstanceOf(ListItemInUseException.class);
    }

    @Test
    void deleteShouldThrowListItemInUseExceptionIfFacilityTypeUsedByFacility() {
        final Integer facilityTypeId = 100;
        final FacilityType facilityType = getFacilityType(facilityTypeId, "Parking", "Parcio");

        final Facility facility = new Facility();
        facility.setId(987);
        facility.setName("Parking Facility");
        facility.setFacilityType(facilityType);

        when(facilityRepository.findAll()).thenReturn(Arrays.asList(facility));

        assertThatThrownBy(() -> adminFacilityService
            .deleteFacilityType(facilityTypeId))
            .isInstanceOf(ListItemInUseException.class);
    }

    private FacilityType getFacilityType(Integer id, String name, String nameCy) {
        final FacilityType facilityType = new FacilityType();
        facilityType.setId(id);
        facilityType.setName(name);
        facilityType.setNameCy(nameCy);

        return facilityType;
    }
}
