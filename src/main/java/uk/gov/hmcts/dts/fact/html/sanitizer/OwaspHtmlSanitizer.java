package uk.gov.hmcts.dts.fact.html.sanitizer;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public final class OwaspHtmlSanitizer {
    private OwaspHtmlSanitizer() {

    }

    /**
     * Sanitize the given HTML string.
     *
     * @param untrustedHtml the HTML string to sanitize
     * @return the sanitized HTML string
     */
    public static String sanitizeHtml(String untrustedHtml) {
        PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("a", "strong", "em")
            .allowUrlProtocols("https", "mailto")
            .allowAttributes("href").onElements("a")
            .requireRelNofollowOnLinks()
            .toFactory();
        return policy.sanitize(untrustedHtml);
    }
}
