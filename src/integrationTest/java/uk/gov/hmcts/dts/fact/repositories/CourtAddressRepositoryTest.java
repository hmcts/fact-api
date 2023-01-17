package uk.gov.hmcts.dts.fact.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.hmcts.dts.fact.entity.CourtAddress;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class CourtAddressRepositoryTest {
    @Autowired
    private CourtAddressRepository courtAddressRepository;

    @Test
    void shouldRetrieveAndUpdateCourtAddresses() {
        final List<CourtAddress> results = courtAddressRepository.findAll();
        assertThat(results).hasSizeGreaterThan(1);

        final List<CourtAddress> newCourtAddresses = new ArrayList<>(results);
        final CourtAddress newAddressToBeAdded = new CourtAddress();
        newAddressToBeAdded.setAddress("1 High Street");
        newAddressToBeAdded.setPostcode("W1A 1AA");
        newAddressToBeAdded.setCourt(results.get(0).getCourt());
        newAddressToBeAdded.setAddressType(results.get(0).getAddressType());
        newAddressToBeAdded.setSortOrder(0);
        newCourtAddresses.add(newAddressToBeAdded);

        final List<CourtAddress> updatedResults = courtAddressRepository.saveAll(newCourtAddresses);
        assertThat(updatedResults).hasSize(results.size() + 1);

        courtAddressRepository.delete(newAddressToBeAdded);
        final List<CourtAddress> finalResults = courtAddressRepository.findAll();
        assertThat(finalResults).hasSize(results.size());
    }
}
