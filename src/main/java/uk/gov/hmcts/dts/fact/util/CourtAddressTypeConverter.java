package uk.gov.hmcts.dts.fact.util;

import uk.gov.hmcts.dts.fact.model.CourtAddress;

import java.util.List;

public class CourtAddressTypeConverter {

    public List<CourtAddress> convertAddressType(final List<CourtAddress> courtAddresses) {
        
        for (final CourtAddress courtAddress : courtAddresses) {
            if (courtAddress.getAddressType().equals("Visit us or write to us")) {
                courtAddress.setAddressType("Visit or contact us");
            } else if (courtAddress.getAddressType().equals("Postal")) {
                courtAddress.setAddressType("Write to us");
            } else if (courtAddress.getAddressType().equals("Visiting")) {
                courtAddress.setAddressType("Visit us");
            }
        }
        
        return courtAddresses;
    }
}
