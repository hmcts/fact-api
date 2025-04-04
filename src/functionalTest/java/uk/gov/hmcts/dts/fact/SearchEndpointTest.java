package uk.gov.hmcts.dts.fact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.model.CourtReferenceWithDistance;
import uk.gov.hmcts.dts.fact.model.CourtWithDistance;
import uk.gov.hmcts.dts.fact.model.ServiceAreaWithCourtReferencesWithDistance;
import uk.gov.hmcts.dts.fact.util.FunctionalTestBase;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
@SuppressWarnings("PMD.TooManyMethods")
class SearchEndpointTest extends FunctionalTestBase {

    private static final String SEARCH_ENDPOINT = "/search/";

    @Test
    void shouldRetrieve10CourtDetailsSortedByDistance() {
        final var response = doGetRequest(SEARCH_ENDPOINT + "results.json?postcode=OX1 1RZ");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtWithDistance> courts = response.body().jsonPath().getList(".", CourtWithDistance.class);
        assertThat(courts.size()).isEqualTo(10);
        assertThat(courts).isSortedAccordingTo(Comparator.comparing(CourtWithDistance::getDistance));
    }

    @Test
    void shouldRetrieve10CourtDetailsByAreaOfLawSortedByDistance() {
        final String aol = "Adoption";
        final var response = doGetRequest(SEARCH_ENDPOINT + "results.json?includeClosed=true&postcode=OX1 1RZ&aol=" + aol);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final List<CourtWithDistance> courts = response.body().jsonPath().getList(".", CourtWithDistance.class);
        assertThat(courts.size()).isEqualTo(10);
        assertThat(courts).isSortedAccordingTo(Comparator.comparing(CourtWithDistance::getDistance));
        assertTrue(courts
                       .stream()
                       .allMatch(c -> c.getAreasOfLaw()
                           .stream()
                           .anyMatch(a -> a.getName().equals(aol))));
    }

    @Test
    void postcodeSearchShouldSupportWelsh() {
        final var welshResponse = doGetRequest(SEARCH_ENDPOINT + "results.json?postcode=CF10 1ET", Map.of(ACCEPT_LANGUAGE, "cy"));
        assertThat(welshResponse.statusCode()).isEqualTo(OK.value());

        final List<CourtWithDistance> welshCourts = welshResponse.body().jsonPath()
            .getList(".", CourtWithDistance.class);
        assertThat(welshCourts.get(0).getAddresses().get(0).getTownName()).isEqualTo("Caerdydd");

        final var englishResponse = doGetRequest(SEARCH_ENDPOINT + "results.json?includeClosed=true&postcode=CF10 1ET");
        assertThat(englishResponse.statusCode()).isEqualTo(OK.value());

        final List<CourtWithDistance> englishCourts = englishResponse.body().jsonPath()
            .getList(".", CourtWithDistance.class);
        assertThat(englishCourts.get(0).getAddresses().get(0).getTownName()).isEqualTo("Cardiff");
    }

    @Test
    void nameSearchShouldSupportWelsh() {
        final var welshResponse = doGetRequest(SEARCH_ENDPOINT + "results.json?q=caerdydd", Map.of(ACCEPT_LANGUAGE, "cy"));
        assertThat(welshResponse.statusCode()).isEqualTo(OK.value());

        final List<CourtWithDistance> welshCourts = welshResponse.body().jsonPath()
            .getList(".", CourtWithDistance.class);
        assertThat(welshCourts.get(0).getAddresses().get(0).getTownName()).isEqualTo("Caerdydd");

        final var englishResponse = doGetRequest(SEARCH_ENDPOINT + "results.json?q=cardiff");
        assertThat(englishResponse.statusCode()).isEqualTo(OK.value());

        final List<CourtWithDistance> englishCourts = englishResponse.body().jsonPath()
            .getList(".", CourtWithDistance.class);
        assertThat(englishCourts.get(0).getAddresses().get(0).getTownName()).isEqualTo("Cardiff");
    }

    @Test
    void shouldRetrieve10CourtReferenceByPostcodeAndServiceAreaSortedByDistance() {
        final String serviceArea = "claims-against-employers";
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?includeClosed=true&postcode=OX1 1RZ&serviceArea=" + serviceArea);

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);

        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts().size()).isEqualTo(10);
        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts()).isSortedAccordingTo(Comparator.comparing(
            CourtReferenceWithDistance::getDistance));
    }

    @Test
    void shouldRetrieve10CourtReferenceByPostcodeAndServiceAreaSupportWelsh() {
        final String serviceArea = "claims-against-employers";
        final var englishResponse = doGetRequest(SEARCH_ENDPOINT + "results?includeClosed=true&postcode=CF24 0RZ&serviceArea=" + serviceArea);
        assertThat(englishResponse.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance englishServiceAreaCourtReferencesWithDistance =
            englishResponse.as(ServiceAreaWithCourtReferencesWithDistance.class);
        assertThat(englishServiceAreaCourtReferencesWithDistance.getCourts().get(0).getName()).isEqualTo(
            "Cardiff  Magistrates' Court");

        final var welshResponse = doGetRequest(
            SEARCH_ENDPOINT + "results?includeClosed=true&postcode=CF24 0RZ&serviceArea=" + serviceArea,
            Map.of(ACCEPT_LANGUAGE, "cy")
        );
        assertThat(welshResponse.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            welshResponse.as(ServiceAreaWithCourtReferencesWithDistance.class);
        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts().get(0).getName()).isEqualTo(
            "Caerdydd  - Llys Ynadon");
    }

    @Test
    void shouldRetrieveRegionalCourtReferenceByPostcodeAndServiceArea() {
        final String serviceArea = "divorce";
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?includeClosed=true&postcode=IP1 2AG&serviceArea=" + serviceArea);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);
        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts().size()).isEqualTo(1);
    }

    @Test
    void shouldRetrieveClosestRegionalCourt() {
        final String serviceArea = "divorce";
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?includeClosed=true&postcode=TR11 2PH&serviceArea=" + serviceArea);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);
        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts().size()).isEqualTo(1);
    }

    @Test
    void shouldRetrieveCourtReferenceByCourtPostcodeAndServiceAreaSortedByDistance() {
        final String serviceArea = "money-claims";
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?postcode=W1U 6PU&serviceArea=" + serviceArea);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);
        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts().size()).isEqualTo(1);
        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts().get(0).getName()).isEqualTo(
            "Central London County Court");
        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts()).isSortedAccordingTo(Comparator.comparing(
            CourtReferenceWithDistance::getDistance));
    }

    @Test
    void shouldRetrieveSpoeCourtReferenceByPostcodeAndServiceArea() {
        final String serviceArea = "childcare-arrangements";
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?includeClosed=true&postcode=B1 1AA&serviceArea=" + serviceArea);
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);
        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts().size()).isEqualTo(1);
        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts().get(0).getName()).isEqualTo(
            "Birmingham Civil and Family Justice Centre");
    }

    @Test
    void shouldRetrieve10CourtReferenceByPostcodeAndActionAndServiceAreaSortedByDistance() {
        final String action = "nearest";
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?postcode=RM19 1SR&serviceArea=money-claims&action=" + action);

        assertThat(response.statusCode()).isEqualTo(OK.value());
        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);

        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts().size()).isEqualTo(10);
        assertThat(serviceAreaWithCourtReferencesWithDistance.getCourts()).isSortedAccordingTo(Comparator.comparing(
            CourtReferenceWithDistance::getDistance));
    }

    @Test
    void shouldReturnOnlyOpenCourtsWhenIncludeClosedIsFalseForMoneyClaims() {
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?includeClosed=false&serviceArea=money-claims&postcode=IP222HF");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);

        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).allMatch(d -> d.equals(
            true)));

    }

    @Test
    void shouldReturnOnlyOpenCourtsWhenIncludeClosedIsDefaultForMoneyClaims() {
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?serviceArea=money-claims&postcode=IP222HF");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);

        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).allMatch(d -> d.equals(
            true)));

    }

    @Test
    void shouldReturnOpenOrClosedCourtsWhenIncludeClosedIsTrueForMoneyClaims() {
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?includeClosed=true&serviceArea=money-claims&postcode=IP222HF");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);

        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).anyMatch(d -> d.equals(
            true)));
        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).anyMatch(d -> d.equals(
            false)));

    }

    @Test
    void shouldReturnOnlyOpenCourtsWhenIncludeClosedIsFalseForTax() {
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?includeClosed=false&serviceArea=tax&postcode=IP222HF");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);

        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).allMatch(d -> d.equals(
            true)));

    }

    @Test
    void shouldReturnOnlyOpenCourtsWhenIncludeClosedIsDefaultForTax() {
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?serviceArea=tax&postcode=IP222HF");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);

        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).allMatch(d -> d.equals(
            true)));

    }

    @Test
    void shouldReturnOpenOrClosedCourtsWhenIncludeClosedIsTrueForTax() {
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?includeClosed=true&serviceArea=tax&postcode=IP222HF");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);

        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).anyMatch(d -> d.equals(
            true)));
        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).anyMatch(d -> d.equals(
            false)));

    }

    @Test
    void shouldReturnOnlyOpenCourtsWhenIncludeClosedIsFalseForProbate() {
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?includeClosed=false&serviceArea=probate&postcode=IP222HF");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);

        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).allMatch(d -> d.equals(
            true)));

    }

    @Test
    void shouldReturnOnlyOpenCourtsWhenIncludeClosedIsDefaultForProbate() {
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?serviceArea=probate&postcode=IP222HF");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);

        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).allMatch(d -> d.equals(
            true)));

    }

    @Test
    void shouldReturnOpenOrClosedCourtsWhenIncludeClosedIsTrueForProbate() {
        final var response = doGetRequest(SEARCH_ENDPOINT + "results?includeClosed=true&serviceArea=probate&postcode=IP222HF");
        assertThat(response.statusCode()).isEqualTo(OK.value());

        final ServiceAreaWithCourtReferencesWithDistance serviceAreaWithCourtReferencesWithDistance =
            response.as(ServiceAreaWithCourtReferencesWithDistance.class);

        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).anyMatch(d -> d.equals(
            true)));
        assertTrue(serviceAreaWithCourtReferencesWithDistance.getCourts().stream().map(CourtReferenceWithDistance::getOpen).anyMatch(d -> d.equals(
            false)));

    }
}
