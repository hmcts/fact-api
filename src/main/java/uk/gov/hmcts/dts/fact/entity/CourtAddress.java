package uk.gov.hmcts.dts.fact.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "search_courtaddress")
@Getter
@Setter
@NoArgsConstructor
public class CourtAddress {
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;
    @OneToOne
    @JoinColumn(name = "address_type_id")
    private AddressType addressType;
    private String address;
    private String addressCy;
    private String townName;
    private String townNameCy;
    private String postcode;

    public CourtAddress(final Court court, final AddressType addressType, final List<String> addressLines, final List<String> addressLinesCy,
                        final String townName, final String townNameCy, final String postcode) {
        this.court = court;
        this.addressType = addressType;
        this.address = convertAddressLines(addressLines);
        this.addressCy = convertAddressLines(addressLinesCy);
        this.townName = townName;
        this.townNameCy = townNameCy;
        this.postcode = postcode;
    }

    private String convertAddressLines(final List<String> addressLines) {
        if (!CollectionUtils.isEmpty(addressLines)) {
            final StringBuilder builder = new StringBuilder();
            final int lastLine = addressLines.size() - 1;
            for (int i = 0; i < lastLine; i++) {
                builder.append(addressLines.get(i))
                    .append(System.lineSeparator());
            }
            builder.append(addressLines.get(lastLine));
            return builder.toString();
        }
        return null;
    }
}
