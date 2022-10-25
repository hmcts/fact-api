package uk.gov.hmcts.dts.fact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.dts.fact.entity.Court;
import uk.gov.hmcts.dts.fact.util.AddressType;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.dts.fact.util.Utils.chooseString;

@Getter
@NoArgsConstructor
@JsonPropertyOrder({"name", "slug", "updated_at"})
public class CourtReference {
    private String name;
    private String slug;
    @JsonProperty("updated_at")
    private String updatedAt;
    private boolean displayed;
    //private String region;
    private List<String> region;

    public CourtReference(uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        this.name = chooseString(courtEntity.getNameCy(), courtEntity.getName());
        this.slug = courtEntity.getSlug();
        this.updatedAt = courtEntity.getUpdatedAt() == null
            ? null : new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(courtEntity.getUpdatedAt());
        this.displayed = courtEntity.getDisplayed();
        this.region = getRegion(courtEntity);
    }

/*    private String getRegion(final uk.gov.hmcts.dts.fact.entity.Court courtEntity){
        List<CourtAddress> addresses = getCourtAddresses(courtEntity);
        return addresses.
    }*/

    private List<uk.gov.hmcts.dts.fact.model.CourtAddress> getCourtAddresses(final uk.gov.hmcts.dts.fact.entity.Court courtEntity) {
        // Return sorted court addresses with 'visit us' or 'visit or contact us' addresses appear first
        System.out.println(courtEntity.getAddresses()
                               .stream()
                               .filter(a -> AddressType.isCourtAddress(a.getAddressType().getName()))

                               //.sorted(comparingInt(a -> AddressType.isCourtAddress(a.getAddressType().getName()) ? 0 : 1))
                               .map(CourtAddress::new)
                               .collect(toList()));
        return courtEntity.getAddresses()
            .stream()
            .filter(a -> AddressType.isCourtAddress(a.getAddressType().getName()))
            //.sorted(comparingInt(a -> AddressType.isCourtAddress(a.getAddressType().getName()) ? 0 : 1))
            .map(CourtAddress::new)
            .collect(toList());
    }

    private List<String> getRegion(final Court courtEntity) {
        // Return sorted court addresses with 'visit us' or 'visit or contact us' addresses appear first
        System.out.println(courtEntity.getAddresses()
                               .stream()
                               .filter(a -> AddressType.isCourtAddress(a.getAddressType().getName()))

                               //.sorted(comparingInt(a -> AddressType.isCourtAddress(a.getAddressType().getName()) ? 0 : 1))
                               .map(CourtAddress::new)
                               .map(CourtAddress::getRegion)
                               .collect(toList()));
        return courtEntity.getAddresses()
            .stream()
            .filter(a -> AddressType.isCourtAddress(a.getAddressType().getName()))

            //.sorted(comparingInt(a -> AddressType.isCourtAddress(a.getAddressType().getName()) ? 0 : 1))
            .map(CourtAddress::new)
            .map(CourtAddress::getRegion)
            .collect(Collectors.toList());
            //.get(0);
        //return "courtEntity.getAddresses()";
//            .stream()
//            .filter((a -> a.getRegion()));
//            .filter(a -> AddressType.isCourtAddress(a.getAddressType().getName()))
//            .map(CourtAddress::new)
    }


}
