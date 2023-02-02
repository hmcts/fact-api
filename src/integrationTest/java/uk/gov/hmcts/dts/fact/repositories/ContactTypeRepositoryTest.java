package uk.gov.hmcts.dts.fact.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.ContactType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class ContactTypeRepositoryTest {
    @Autowired
    private ContactTypeRepository contactTypeRepository;

    @Test
    void shouldRetrieveContactTypes() {
        List<ContactType> results = contactTypeRepository.findAll();
        assertThat(results).hasSizeGreaterThan(1);
        assertThat(results.stream().map(r -> r.getDescription())).isNotEmpty();
        assertThat(results.stream().map(r -> r.getDescriptionCy())).isNotEmpty();
    }
}
