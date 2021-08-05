package uk.gov.hmcts.dts.fact.services.admin.list;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.FacilityType;
import uk.gov.hmcts.dts.fact.repositories.FacilityTypeRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminFacilityService.class)
public class AdminFacilityServiceTest {

    private static final int FACILITY_TYPE_COUNT = 3;

    @Autowired
    private AdminFacilityService adminFacilityService;

    @MockBean
    private FacilityTypeRepository facilityTypeRepository;

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
}
