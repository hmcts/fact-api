package uk.gov.hmcts.dts.fact.mapit;

import feign.FeignException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
//@Disabled("Need to investigate why Mapit Key not getting picked up")
class MapitClientTest {

    @Autowired
    private MapitClient mapitClient;

    @Test
    @SuppressWarnings("PMD.UseUnderscoresInNumericLiterals")
    void shouldReturnPartialPostcodeDataSuccess() {
        final MapitData mapitData = mapitClient.getMapitDataWithPartial("OX1");
        assertThat(mapitData.hasLatAndLonValues()).isEqualTo(true);
    }

    @Test
    void shouldFailForNonExistingPartialPostcode() {
        try {
            mapitClient.getMapitDataWithPartial("AA1");
            fail("Did not expect to find a result for the invalid partial postcode of AA1");
        } catch (final FeignException ex) {
            assertThat(ex.status()).isEqualTo(404);
        }
    }

    @Test
    void shouldFailForBadPartialPostcode() {
        try {
            mapitClient.getMapitDataWithPartial("OX1 2B");
            fail("Did not expect to find a result for the invalid partial postcode of OX1 2B");
        } catch (final FeignException ex) {
            assertThat(ex.status()).isEqualTo(400);
        }
    }

    @Test
    @SuppressWarnings("PMD.UseUnderscoresInNumericLiterals")
    void shouldReturnExpectedCoordinatesForPostcode() {
        final MapitData mapitData = mapitClient.getMapitData("OX1 1RZ");
        assertThat(mapitData.hasLatAndLonValues()).isEqualTo(true);
    }

    @Test
    void shouldReturnBlankCoordinatesForUnsupportedPostcode() {
        final MapitData mapitData = mapitClient.getMapitData("JE3 4BA");
        assertThat(mapitData.hasLatAndLonValues()).isEqualTo(false);
    }

    @Test
    void shouldThrowFeignExceptionForPartialPostcode() {
        try {
            mapitClient.getMapitData("OX1");
            fail();
        } catch (final FeignException ex) {
            assertThat(ex.status()).isEqualTo(400);
        }
    }

    @Test
    void shouldReturnLocalAuthoritiyForPostcodeWhenMapitReturnsObject() {
        final MapitData mapitData = mapitClient.getMapitData("OX1 1RZ");
        final Optional<String> localAuthority = mapitData.getLocalAuthority();
        assertThat(localAuthority.isPresent()).isEqualTo(true);
        assertThat(localAuthority.get()).isEqualTo("Oxfordshire County Council");
    }


    @Test
    void shouldReturnLocalAuthoritiyForPostcodeWhenMapitReturnsValue() {
        final MapitData mapitData = mapitClient.getMapitData("sa70 7au");
        final Optional<String> localAuthority = mapitData.getLocalAuthority();
        assertThat(localAuthority.isPresent()).isEqualTo(true);
        assertThat(localAuthority.get()).isEqualTo("Pembrokeshire Council");
    }

    @Test
    void shouldReturnAreaInformationForValidLocalAuthorityName() {
        final var mapitAreaInfo =
            mapitClient.getMapitDataForLocalAuthorities("Birmingham City Council");
        assertThat(mapitAreaInfo.values().size()).isGreaterThan(0);
    }

    @Test
    void shouldReturnNoAreaInformationForInvalidLocalAuthorityName() {
        final var mapitAreaInfo = mapitClient.getMapitDataForLocalAuthorities("Birm Council City");
        assertThat(mapitAreaInfo.isEmpty()).isTrue();
    }
}
