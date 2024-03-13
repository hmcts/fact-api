package uk.gov.hmcts.dts.fact.util;

import org.springframework.core.convert.converter.Converter;

import static uk.gov.hmcts.dts.fact.util.Action.findAction;

/**
 * Convert a string to an Action.
 */
public class ActionConverter implements Converter<String, Action> {

    /**
     * Convert the source string to an Action.
     *
     * @param source the source string
     * @return the Action
     */
    @Override
    public Action convert(String source) {
        try {
            return findAction(source);
        } catch (IllegalArgumentException e) {
            return convert("undefined");
        }
    }
}
