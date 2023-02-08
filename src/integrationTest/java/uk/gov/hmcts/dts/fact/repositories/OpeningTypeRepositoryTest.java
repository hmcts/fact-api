package uk.gov.hmcts.dts.fact.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.OpeningType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class OpeningTypeRepositoryTest {
    @Autowired
    private OpeningTypeRepository openingTypeRepository;

    @Test
    void shouldRetrieveAllOpeningTypes() {
        List<OpeningType> results = openingTypeRepository.findAll();
        assertThat(results).hasSize(12);
        assertThat(results.stream().map(r -> r.getDescription())).isNotEmpty();
        assertThat(results.stream().map(r -> r.getDescriptionCy())).isNotEmpty();
    }
}
