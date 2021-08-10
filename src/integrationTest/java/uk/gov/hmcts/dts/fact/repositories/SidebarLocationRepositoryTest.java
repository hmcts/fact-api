package uk.gov.hmcts.dts.fact.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class SidebarLocationRepositoryTest {
    @Autowired
    private SidebarLocationRepository sidebarLocationRepository;

    @Test
    void shouldRetrieveSidebarLocationByName() {
        assertThat(sidebarLocationRepository.findSidebarLocationByName("This location handles")).isPresent();
    }

    @Test
    void shouldNotRetrieveSidebarLocationUsingIncorrectName() {
        assertThat(sidebarLocationRepository.findSidebarLocationByName("Incorrect location")).isNotPresent();
    }
}
