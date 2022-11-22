package uk.gov.hmcts.dts.fact.services.admin.list;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.admin.AddressType;
import uk.gov.hmcts.dts.fact.repositories.AddressTypeRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AdminAddressTypeService.class)
class AdminAddressTypeServiceTest {
    private static final int TEST_TYPE_ID1 = 1;
    private static final int TEST_TYPE_ID2 = 2;
    private static final int TEST_TYPE_ID3 = 3;
    private static final String TEST_TYPE1 = "Visit us";
    private static final String TEST_TYPE2 = "Write to us";
    private static final String TEST_TYPE3 = "Visit or contact us";
    private static final String TEST_TYPE_CY1 = TEST_TYPE1 + " cy";
    private static final String TEST_TYPE_CY2 = TEST_TYPE2 + " cy";
    private static final String TEST_TYPE_CY3 = TEST_TYPE3 + " cy";

    private static final uk.gov.hmcts.dts.fact.entity.AddressType ADDRESS_TYPE1 = new uk.gov.hmcts.dts.fact.entity.AddressType(TEST_TYPE_ID1, TEST_TYPE1, TEST_TYPE_CY1);
    private static final uk.gov.hmcts.dts.fact.entity.AddressType ADDRESS_TYPE2 = new uk.gov.hmcts.dts.fact.entity.AddressType(TEST_TYPE_ID2, TEST_TYPE2, TEST_TYPE_CY2);
    private static final uk.gov.hmcts.dts.fact.entity.AddressType ADDRESS_TYPE3 = new uk.gov.hmcts.dts.fact.entity.AddressType(TEST_TYPE_ID3, TEST_TYPE3, TEST_TYPE_CY3);
    private static final List<uk.gov.hmcts.dts.fact.entity.AddressType> ADDRESS_TYPES = Arrays.asList(ADDRESS_TYPE1, ADDRESS_TYPE2, ADDRESS_TYPE3);

    @Autowired
    private AdminAddressTypeService adminService;

    @MockBean
    private AddressTypeRepository addressTypeRepository;

    @Test
    void shouldReturnAllAddressTypes() {
        when(addressTypeRepository.findAll()).thenReturn(ADDRESS_TYPES);
        final List<AddressType> results = adminService.getAllAddressTypes();

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(results).hasSize(ADDRESS_TYPES.size());

        softly.assertThat(results.get(0).getId()).isEqualTo(TEST_TYPE_ID1);
        softly.assertThat(results.get(0).getName()).isEqualTo(TEST_TYPE1);
        softly.assertThat(results.get(0).getNameCy()).isEqualTo(TEST_TYPE_CY1);

        softly.assertThat(results.get(1).getId()).isEqualTo(TEST_TYPE_ID2);
        softly.assertThat(results.get(1).getName()).isEqualTo(TEST_TYPE2);
        softly.assertThat(results.get(1).getNameCy()).isEqualTo(TEST_TYPE_CY2);

        softly.assertThat(results.get(2).getId()).isEqualTo(TEST_TYPE_ID3);
        softly.assertThat(results.get(2).getName()).isEqualTo(TEST_TYPE3);
        softly.assertThat(results.get(2).getNameCy()).isEqualTo(TEST_TYPE_CY3);

        softly.assertAll();
    }

    @Test
    void shouldRetrieveAddressTypeMap() {
        when(addressTypeRepository.findAll()).thenReturn(ADDRESS_TYPES);
        final Map<Integer, uk.gov.hmcts.dts.fact.entity.AddressType> results = adminService.getAddressTypeMap();

        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(results).hasSize(3);
        softly.assertThat(results.get(TEST_TYPE_ID1)).isEqualTo(ADDRESS_TYPE1);
        softly.assertThat(results.get(TEST_TYPE_ID2)).isEqualTo(ADDRESS_TYPE2);
        softly.assertThat(results.get(TEST_TYPE_ID3)).isEqualTo(ADDRESS_TYPE3);
        softly.assertAll();
    }
}
