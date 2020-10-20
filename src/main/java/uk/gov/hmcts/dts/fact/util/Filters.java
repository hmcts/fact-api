package uk.gov.hmcts.dts.fact.util;

import uk.gov.hmcts.dts.fact.model.Contact;

import java.util.List;

import static java.util.stream.Collectors.toList;

public final class Filters {

    private Filters() {

    }

    public static List<String> extractDxContacts(List<Contact> contacts) {
        List<Contact> dx = contacts
            .stream()
            .filter(c -> "DX".equals(c.getName()))
            .collect(toList());
        return dx
            .stream()
            .map(Contact::getNumber)
            .collect(toList());
    }

    public static List<Contact> removeDxContacts(List<Contact> contacts) {
        return contacts
            .stream()
            .filter(c -> !"DX".equals(c.getName()))
            .collect(toList());
    }

    public static String stripHtmlFromString(String text) {
        if (text != null) {
            return text.replaceAll("\\<.*?>|&nbsp;|amp;", "");
        }
        return "";
    }

}
