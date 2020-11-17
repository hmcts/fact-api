package uk.gov.hmcts.dts.fact.services.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.entity.ServiceAreaCourt;
import uk.gov.hmcts.dts.fact.mapit.MapitData;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.CIVIL;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.FAMILY;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.OTHER;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PostcodeSearchForServiceAreaFactory.class)
class PostcodeSearchForServiceAreaFactoryTest {

    private static final String LOCAL_AUTHORITY = "local-authority";
    private static final String REGIONAL = "regional";

    @Autowired
    private PostcodeSearchForServiceAreaFactory postcodeSearchForServiceAreaFactory;

    @MockBean
    private NearestRegionalByAreaOfLawAndLocalAuthoritySearch nearestRegionalByAreaOfLawAndLocalAuthoritySearch;

    @MockBean
    private NearestTenByAreaOfLawAndLocalAuthoritySearch nearestTenByAreaOfLawAndLocalAuthoritySearch;

    @MockBean
    private NearestCourtsByCourtPostcodeAndAreaOfLawSearch nearestCourtsByCourtPostcodeAndAreaOfLawSearch;

    @MockBean
    private NearestCourtsByPostcodeAndAreaOfLawSearch nearestCourtsByPostcodeAndAreaOfLawSearch;

    @Test
    void shouldReturnFamilyRegionalSearch() {
        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);
        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        when(serviceArea.getType()).thenReturn(FAMILY.toString());
        when(serviceArea.getCatchmentMethod()).thenReturn(LOCAL_AUTHORITY);
        when(serviceArea.getServiceAreaCourts()).thenReturn(singletonList(serviceAreaCourt));
        when(serviceAreaCourt.getCatchmentType()).thenReturn(REGIONAL);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of("Suffolk County Council"));

        final Search search = postcodeSearchForServiceAreaFactory.getSearchFor(serviceArea, mapitData);

        assertThat(search).isEqualTo(nearestRegionalByAreaOfLawAndLocalAuthoritySearch);
    }

    @Test
    void shouldReturnFamilyLocalAuthoritySearch() {
        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);
        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        when(serviceArea.getType()).thenReturn(FAMILY.toString());
        when(serviceArea.getCatchmentMethod()).thenReturn(LOCAL_AUTHORITY);
        when(serviceArea.getServiceAreaCourts()).thenReturn(singletonList(serviceAreaCourt));
        when(serviceAreaCourt.getCatchmentType()).thenReturn("national");
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of("Suffolk County Council"));

        final Search search = postcodeSearchForServiceAreaFactory.getSearchFor(serviceArea, mapitData);

        assertThat(search).isEqualTo(nearestTenByAreaOfLawAndLocalAuthoritySearch);
    }
    
    @Test
    void shouldReturnPostcodeSearchIfNoLocalAuthorityInMapitData() {
        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);
        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        when(serviceArea.getType()).thenReturn(FAMILY.toString());
        when(serviceArea.getCatchmentMethod()).thenReturn(LOCAL_AUTHORITY);
        when(serviceArea.getServiceAreaCourts()).thenReturn(singletonList(serviceAreaCourt));
        when(serviceAreaCourt.getCatchmentType()).thenReturn("national");
        when(mapitData.getLocalAuthority()).thenReturn(empty());

        final Search search = postcodeSearchForServiceAreaFactory.getSearchFor(serviceArea, mapitData);

        assertThat(search).isEqualTo(nearestCourtsByPostcodeAndAreaOfLawSearch);
    }

    @Test
    void shouldReturnPostcodeSearchIfCatchmentMethodDoesNotMatchLocalAuthority() {
        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);
        final ServiceAreaCourt serviceAreaCourt = mock(ServiceAreaCourt.class);

        when(serviceArea.getType()).thenReturn(FAMILY.toString());
        when(serviceArea.getCatchmentMethod()).thenReturn("Something else");
        when(serviceArea.getServiceAreaCourts()).thenReturn(singletonList(serviceAreaCourt));
        when(serviceAreaCourt.getCatchmentType()).thenReturn("national");
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of("Suffolk County Council"));

        final Search search = postcodeSearchForServiceAreaFactory.getSearchFor(serviceArea, mapitData);

        assertThat(search).isEqualTo(nearestCourtsByPostcodeAndAreaOfLawSearch);
    }

    @Test
    void shouldReturnCivilSearch() {
        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);

        when(serviceArea.getType()).thenReturn(CIVIL.toString());

        final Search search = postcodeSearchForServiceAreaFactory.getSearchFor(serviceArea, mapitData);

        assertThat(search).isEqualTo(nearestCourtsByCourtPostcodeAndAreaOfLawSearch);
    }

    @Test
    void shouldReturnPostcodeSearch() {
        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);

        when(serviceArea.getType()).thenReturn(OTHER.toString());

        final Search search = postcodeSearchForServiceAreaFactory.getSearchFor(serviceArea, mapitData);

        assertThat(search).isEqualTo(nearestCourtsByPostcodeAndAreaOfLawSearch);
    }
}