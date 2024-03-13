package uk.gov.hmcts.dts.fact.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for validating UK postcode areas.
 */
@Slf4j
public final class PostcodeArea {
    private static final String UK_POSTCODE_AREAS_FILE = "ukPostcodeAreas.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static List<String> ukPostcodeAreas;

    static {
        try {
            ukPostcodeAreas = getPostcodeAreasFromResource(UK_POSTCODE_AREAS_FILE);
        } catch (final IOException e) {
            log.error("Could not load resource {}", UK_POSTCODE_AREAS_FILE);
        }
    }

    private PostcodeArea() {
    }

    /**
     * Check if a string is a valid UK postcode area.
     *
     * @param area the area to check
     * @return true if the area is a valid UK postcode area
     */
    public static boolean isValidArea(final String area) {
        if (StringUtils.isBlank(area)) {
            return false;
        }
        return area.length() < 3 && ukPostcodeAreas.contains(area.toUpperCase(Locale.getDefault()));
    }

    /**
     * Get the list of valid UK postcode areas.
     *
     * @return the list of valid UK postcode areas
     */
    private static List<String> getPostcodeAreasFromResource(String resource) throws IOException {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            final String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return Arrays.asList(OBJECT_MAPPER.readValue(json, String[].class));
        }
    }
}
