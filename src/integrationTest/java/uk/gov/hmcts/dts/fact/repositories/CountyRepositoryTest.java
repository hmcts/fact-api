package uk.gov.hmcts.dts.fact.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.County;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class CountyRepositoryTest {
    @Autowired
    private CountyRepository countyRepository;

    @Test
    void shouldRetrieveCounties() {
        List<County> results = countyRepository.findAll();
        assertThat(results).hasSizeGreaterThan(1);
        assertThat(results.stream().map(r -> r.getName())).isNotEmpty();
        assertThat(results.stream().map(r -> r.getCountry())).isNotEmpty();
    }
}
