package uk.gov.hmcts.dts.fact.util;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TestUtil {
    public static final String COURTS_ENDPOINT = "/courts/";
    public static final String ADMIN_COURTS_ENDPOINT = "/admin/courts/";
    public static final String BEARER = "Bearer ";

    private TestUtil() {
    }

    public static ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
