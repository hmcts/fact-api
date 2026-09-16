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
        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
        return policy.sanitize(untrustedHtml);
    }
}
