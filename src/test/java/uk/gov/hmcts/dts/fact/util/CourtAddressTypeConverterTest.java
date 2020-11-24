package uk.gov.hmcts.dts.fact.util;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.dts.fact.model.CourtAddress;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class CourtAddressTypeConverterTest {

    @Test
    void shouldConvertAddressTypesToCorrectedTypes() {

        final CourtAddress courtAddress1 = new CourtAddress();
        final CourtAddress courtAddress2 = new CourtAddress();
        final CourtAddress courtAddress3 = new CourtAddress();
        final CourtAddress courtAddress4 = new CourtAddress();

        courtAddress1.setAddressType("Visit us or write to us");
        courtAddress2.setAddressType("Postal");
        courtAddress3.setAddressType("Visiting");
        courtAddress4.setAddressType("Something else");

        final List<CourtAddress> courtAddresses = asList(courtAddress1, courtAddress2, courtAddress3, courtAddress4);

        final List<CourtAddress> results = new CourtAddressTypeConverter().convertAddressType(courtAddresses);

        assertThat(results).hasSize(4);
        assertThat(results.get(0).getAddressType()).isEqualTo("Visit or contact us");
        assertThat(results.get(1).getAddressType()).isEqualTo("Write to us");
        assertThat(results.get(2).getAddressType()).isEqualTo("Visit us");
        assertThat(results.get(3).getAddressType()).isEqualTo("Something else");
    }
}