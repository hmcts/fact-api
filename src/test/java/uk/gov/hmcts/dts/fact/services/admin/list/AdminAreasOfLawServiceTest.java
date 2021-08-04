package uk.gov.hmcts.dts.fact.services.admin.list;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.dts.fact.entity.AreaOfLaw;
import uk.gov.hmcts.dts.fact.repositories.AreasOfLawRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = AdminAreasOfLawService.class)
public class AdminAreasOfLawServiceTest {

    @Autowired
    private AdminAreasOfLawService areasOfLawService;

    @MockBean
    private AreasOfLawRepository areasOfLawRepository;


    @Test
    void shouldReturnAllAreasOfLaw() {
        final List<AreaOfLaw> mockAreasOfLaw = Arrays.asList(
            new AreaOfLaw(100, "Divorce"),
            new AreaOfLaw(200, "Children"),
            new AreaOfLaw(300, "Money")
        );
        when(areasOfLawRepository.findAll()).thenReturn(mockAreasOfLaw);

        final List<uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw> expectedResult = mockAreasOfLaw
            .stream()
            .map(uk.gov.hmcts.dts.fact.model.admin.AreaOfLaw::new)
            .collect(Collectors.toList());

        assertThat(areasOfLawService.getAllAreasOfLaw()).isEqualTo(expectedResult);
    }
}
