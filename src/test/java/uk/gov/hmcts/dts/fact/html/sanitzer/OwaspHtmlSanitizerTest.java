package uk.gov.hmcts.dts.fact.html.sanitzer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.dts.fact.html.sanitizer.OwaspHtmlSanitizer.sanitizeHtml;


class OwaspHtmlSanitizerTest {
    @Test
    void shouldSanitizeHtml() {
        assertThat(sanitizeHtml("<p>urgent notice</p>")).isEqualTo("urgent notice");
        assertThat(sanitizeHtml("<p>urgent notice <a hrfe='#'>https://www.google.com</a></p>"))
            .isEqualTo("urgent notice https://www.google.com");
        assertThat(sanitizeHtml("<p>urgent notice <strong>bold</strong></p>"))
            .isEqualTo("urgent notice <strong>bold</strong>");
    }
}
