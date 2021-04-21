package uk.gov.hmcts.dts.fact.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class TestUtil {
    public static final String CONTENT_TYPE_VALUE = "application/json";
    public static final String COURTS_ENDPOINT = "/courts/";
    public static final String BEARER = "Bearer ";

    private TestUtil() {
    }

    public static ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
