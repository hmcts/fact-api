package uk.gov.hmcts.dts.fact.services.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.ServiceArea;
import uk.gov.hmcts.dts.fact.mapit.MapitData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.CIVIL;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.FAMILY;
import static uk.gov.hmcts.dts.fact.model.ServiceAreaType.OTHER;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ServiceAreaSearchFactory.class)
class ServiceAreaSearchFactoryTest {

    @Autowired
    private ServiceAreaSearchFactory serviceAreaSearchFactory;

    @MockBean
    private DefaultSearch defaultSearch;

    @MockBean
    private FamilySearchFactory familySearchFactory;

    @MockBean
    private CivilSearch civilSearch;

    @Test
    void shouldReturnFamilySearch() {

        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);
        final Search search = mock(Search.class);

        when(serviceArea.getType()).thenReturn(FAMILY.toString());
        when(familySearchFactory.getSearchFor(serviceArea, mapitData)).thenReturn(search);

        final Search result = serviceAreaSearchFactory.getSearchFor(serviceArea, mapitData);

        assertThat(result).isEqualTo(search);
        verify(familySearchFactory).getSearchFor(serviceArea, mapitData);
    }

    @Test
    void shouldReturnCivilSearch() {

        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);

        when(serviceArea.getType()).thenReturn(CIVIL.toString());

        final Search result = serviceAreaSearchFactory.getSearchFor(serviceArea, mapitData);

        assertThat(result).isEqualTo(civilSearch);
    }

    @Test
    void shouldReturnDefaultSearch() {

        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);

        when(serviceArea.getType()).thenReturn(OTHER.toString());

        final Search result = serviceAreaSearchFactory.getSearchFor(serviceArea, mapitData);

        assertThat(result).isEqualTo(defaultSearch);
    }

    @Test
    void shouldReturnDefaultSearchForActionNearest() {

        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);
        final String action = "nearest";

        when(serviceArea.getType()).thenReturn(OTHER.toString());

        final Search result = serviceAreaSearchFactory.getSearchForNearest(serviceArea, mapitData, action);

        assertThat(result).isEqualTo(defaultSearch);
    }

    @Test
    void shouldReturnCivilSearchForActionBlank() {

        final ServiceArea serviceArea = mock(ServiceArea.class);
        final MapitData mapitData = mock(MapitData.class);
        final String action = "";

        when(serviceArea.getType()).thenReturn(CIVIL.toString());

        final Search result = serviceAreaSearchFactory.getSearchForNearest(serviceArea, mapitData, action);

        assertThat(result).isEqualTo(civilSearch);
    }
}
