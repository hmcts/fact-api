package uk.gov.hmcts.dts.fact.services.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.entity.CourtAdditionalLink;
import uk.gov.hmcts.dts.fact.entity.SidebarLocation;
import uk.gov.hmcts.dts.fact.exception.NotFoundException;
import uk.gov.hmcts.dts.fact.model.admin.AdditionalLink;
import uk.gov.hmcts.dts.fact.repositories.CourtAdditionalLinkRepository;
import uk.gov.hmcts.dts.fact.repositories.CourtRepository;
import uk.gov.hmcts.dts.fact.services.admin.list.AdminSidebarLocationService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminCourtAdditionalLinkService.class)
public class AdminCourtAdditionalLinkServiceTest {
    private static final String TEST_SLUG = "court-slug";
    private static final Court MOCK_COURT = mock(Court.class);

    private static final String TEST_URL1 = "www.test1.com";
    private static final String TEST_URL2 = "www.test2.com";
    private static final String TEST_URL3 = "www.test3.com";

    private static final String TEST_DESCRIPTION1 = "description 1";
    private static final String TEST_DESCRIPTION2 = "description 2";
    private static final String TEST_DESCRIPTION3 = "description 3";

    private static final String TEST_DESCRIPTION_CY1 = "description cy 1";
    private static final String TEST_DESCRIPTION_CY2 = "description cy 2";
    private static final String TEST_DESCRIPTION_CY3 = "description cy 3";

    private static final String TEST_TYPE1 = "type 1";
    private static final String TEST_TYPE2 = "type 2";
    private static final String TEST_TYPE3 = "type 3";

    private static final Integer TEST_COURT_ID = 20;

    private static final String THIS_LOCATION_HANDLES_LOCATION_NAME = "This location handles";
    private static final String FIND_OUT_MORE_ABOUT_LOCATION_NAME = "Find out more about";

    private static final List<AdditionalLink> EXPECTED_ADDITIONAL_LINKS = Arrays.asList(
        new AdditionalLink(1, TEST_URL1, TEST_DESCRIPTION1, TEST_DESCRIPTION_CY1, TEST_TYPE1),
        new AdditionalLink(1, TEST_URL2, TEST_DESCRIPTION2, TEST_DESCRIPTION_CY2, TEST_TYPE2),
        new AdditionalLink(2, TEST_URL3, TEST_DESCRIPTION3, TEST_DESCRIPTION_CY3, TEST_TYPE3)
    );

    private static final SidebarLocation THIS_LOCATION_HANDLES_SIDEBAR_LOCATION = new SidebarLocation(1, THIS_LOCATION_HANDLES_LOCATION_NAME);
    private static final SidebarLocation FIND_OUT_MORE_ABOUT_SIDEBAR_LOCATION = new SidebarLocation(2, FIND_OUT_MORE_ABOUT_LOCATION_NAME);

    private static final uk.gov.hmcts.dts.fact.entity.AdditionalLink ADDITIONAL_LINK_ENTITY1 = new uk.gov.hmcts.dts.fact.entity.AdditionalLink(TEST_URL1,
                                                                                                                                               TEST_DESCRIPTION1,
                                                                                                                                               TEST_DESCRIPTION_CY1,
                                                                                                                                               TEST_TYPE1,
                                                                                                                                               THIS_LOCATION_HANDLES_SIDEBAR_LOCATION);
    private static final uk.gov.hmcts.dts.fact.entity.AdditionalLink ADDITIONAL_LINK_ENTITY2 = new uk.gov.hmcts.dts.fact.entity.AdditionalLink(TEST_URL2,
                                                                                                                                               TEST_DESCRIPTION2,
                                                                                                                                               TEST_DESCRIPTION_CY2,
                                                                                                                                               TEST_TYPE2,
                                                                                                                                               FIND_OUT_MORE_ABOUT_SIDEBAR_LOCATION);
    private static final uk.gov.hmcts.dts.fact.entity.AdditionalLink ADDITIONAL_LINK_ENTITY3 = new uk.gov.hmcts.dts.fact.entity.AdditionalLink(TEST_URL3,
                                                                                                                                               TEST_DESCRIPTION3,
                                                                                                                                               TEST_DESCRIPTION_CY3,
                                                                                                                                               TEST_TYPE3,
                                                                                                                                               FIND_OUT_MORE_ABOUT_SIDEBAR_LOCATION);
    private static final List<CourtAdditionalLink> COURT_ADDITIONAL_LINKS = Arrays.asList(
        new CourtAdditionalLink(MOCK_COURT, ADDITIONAL_LINK_ENTITY1, 0),
        new CourtAdditionalLink(MOCK_COURT, ADDITIONAL_LINK_ENTITY2, 1),
        new CourtAdditionalLink(MOCK_COURT, ADDITIONAL_LINK_ENTITY3, 2)
    );

    private static final String NOT_FOUND = "Not found: ";

    @Autowired
    private AdminCourtAdditionalLinkService adminService;

    @MockBean
    private CourtRepository courtRepository;

    @MockBean
    private CourtAdditionalLinkRepository courtAdditionalLinkRepository;

    @MockBean
    private AdminSidebarLocationService sidebarLocationService;

    @Test
    void shouldRetrieveCourtAdditionalLinks() {
        when(MOCK_COURT.getCourtAdditionalLinks()).thenReturn(COURT_ADDITIONAL_LINKS);
        when(courtRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.of(MOCK_COURT));

        final List<AdditionalLink> results = adminService.getCourtAdditionalLinksBySlug(TEST_SLUG);
        assertThat(results).hasSize(COURT_ADDITIONAL_LINKS.size());
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingAdditionalLinksForNonExistentCourt() {
        when(courtRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getCourtAdditionalLinksBySlug(TEST_SLUG))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + TEST_SLUG);
    }

    @Test
    void shouldRetrieveInPersonCourtCourtCasesHeardAdditionalLinks() {
        when(MOCK_COURT.isInPerson()).thenReturn(true);
        when(MOCK_COURT.getCourtAdditionalLinks()).thenReturn(COURT_ADDITIONAL_LINKS);

        final List<AdditionalLink> results = adminService.getCourtCasesHeardAdditionalLinks(MOCK_COURT);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSidebarLocationId()).isEqualTo(THIS_LOCATION_HANDLES_SIDEBAR_LOCATION.getId());
    }

    @Test
    void shouldRetrieveNotInPersonCourtCasesHeardAdditionalLinks() {
        when(MOCK_COURT.isInPerson()).thenReturn(false);
        when(MOCK_COURT.getCourtAdditionalLinks()).thenReturn(COURT_ADDITIONAL_LINKS);

        final List<AdditionalLink> results = adminService.getCourtCasesHeardAdditionalLinks(MOCK_COURT);
        assertThat(results).hasSize(2);
        assertThat(results.stream().map(r -> r.getSidebarLocationId())).allSatisfy(r -> FIND_OUT_MORE_ABOUT_SIDEBAR_LOCATION.getId());
    }

    @Test
    void shouldUpdateCourtAdditionalLinks() {
        when(MOCK_COURT.getId()).thenReturn(TEST_COURT_ID);
        when(courtRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.of(MOCK_COURT));
        when(sidebarLocationService.getSidebarLocationMap()).thenReturn(Map.of(1, THIS_LOCATION_HANDLES_SIDEBAR_LOCATION,
                                                                               2, FIND_OUT_MORE_ABOUT_SIDEBAR_LOCATION));
        when(courtAdditionalLinkRepository.saveAll(any())).thenReturn(COURT_ADDITIONAL_LINKS);

        final List<AdditionalLink> results = adminService.updateCourtAdditionalLinks(TEST_SLUG, EXPECTED_ADDITIONAL_LINKS);
        assertThat(results)
            .hasSize(EXPECTED_ADDITIONAL_LINKS.size())
            .containsAnyElementsOf(EXPECTED_ADDITIONAL_LINKS);

        verify(courtAdditionalLinkRepository).deleteCourtAdditionalLinksByCourtId(TEST_COURT_ID);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingAdditionalLinksForNonExistentCourt() {
        when(courtRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateCourtAdditionalLinks(TEST_SLUG, any()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NOT_FOUND + TEST_SLUG);
    }
}
