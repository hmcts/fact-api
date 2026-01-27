package uk.gov.hmcts.dts.fact.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "postcode_search_usage")
public class PostcodeSearchUsage {

    private static final Pattern TRAILING_ALPHA_PATTERN = Pattern.compile("\\D+$");

    @Id
    @SequenceGenerator(name = "seq-gen", sequenceName = "postcode_search_usage_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    private Integer id;
    @Column(name = "full_postcode")
    private String fullPostcode;
    @Column(name = "cache_postcode")
    private String cachePostcode;
    @Column(name = "search_timestamp")
    @CreationTimestamp
    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    private LocalDateTime searchTimestamp;

    /**
     * Convenience method to turn a postcode into a valid {@link PostcodeSearchUsage} record.
     *
     * @param postcode the postcode
     * @return a {@link PostcodeSearchUsage} record
     * @throws NullPointerException if postcode is null
     */
    public static PostcodeSearchUsage fromPostcode(@NonNull String postcode) {
        String cachePostcode = TRAILING_ALPHA_PATTERN.matcher(postcode).replaceAll("");
        PostcodeSearchUsage usage = new PostcodeSearchUsage();
        usage.setFullPostcode(postcode);
        usage.setCachePostcode(cachePostcode);
        return usage;
    }
}
