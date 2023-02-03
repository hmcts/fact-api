package uk.gov.hmcts.dts.fact.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TestHelper {
    private TestHelper() {
    }

    public static String getResourceAsJson(String resource) throws IOException {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
