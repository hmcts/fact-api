package uk.gov.hmcts.dts.fact.services.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FamilySearchFactory.class)
class FamilySearchFactoryTest {

    private static final String LOCAL_AUTHORITY = "local-authority";

    @Autowired
    private FamilySearchFactory familySearchFactory;

    @MockitoBean
    private FamilyRegionalSearch familyRegionalSearch;

    @MockitoBean
    private FamilyNonRegionalSearch familyNonRegionalSearch;

    @MockitoBean
    private DefaultSearch defaultSearch;

    @Test
    void shouldReturnFamilyRegionalSearch() {

        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);

        when(serviceArea.getCatchmentMethod()).thenReturn(LOCAL_AUTHORITY);
        when(serviceArea.isRegional()).thenReturn(true);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of("Suffolk County Council"));

        final Search search = familySearchFactory.getSearchFor(serviceArea, mapitData);

        assertThat(search).isEqualTo(familyRegionalSearch);
    }

    @Test
    void shouldReturnFamilyNonRegionalSearch() {

        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);

        when(serviceArea.getCatchmentMethod()).thenReturn(LOCAL_AUTHORITY);
        when(serviceArea.isRegional()).thenReturn(false);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of("Suffolk County Council"));

        final Search search = familySearchFactory.getSearchFor(serviceArea, mapitData);

        assertThat(search).isEqualTo(familyNonRegionalSearch);
    }

    @Test
    void shouldReturnDefaultSearchIfCatchmentMethodIsNotLocalAuthority() {

        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);

        when(serviceArea.getCatchmentMethod()).thenReturn("something-else");
        when(serviceArea.isRegional()).thenReturn(false);
        when(mapitData.getLocalAuthority()).thenReturn(Optional.of("Suffolk County Council"));

        final Search search = familySearchFactory.getSearchFor(serviceArea, mapitData);

        assertThat(search).isEqualTo(defaultSearch);
    }

    @Test
    void shouldReturnDefaultSearchIfLocalAuthorityNameIsNotPresent() {

        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);

        when(serviceArea.getCatchmentMethod()).thenReturn(LOCAL_AUTHORITY);
        when(serviceArea.isRegional()).thenReturn(false);
        when(mapitData.getLocalAuthority()).thenReturn(empty());

        final Search search = familySearchFactory.getSearchFor(serviceArea, mapitData);

        assertThat(search).isEqualTo(defaultSearch);
    }
}
