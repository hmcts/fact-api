package uk.gov.hmcts.dts.fact.util;

import org.springframework.core.convert.converter.Converter;

import static uk.gov.hmcts.dts.fact.util.Action.findAction;

public class ActionConverter implements Converter<String, Action> {
    @Override
    public Action convert(String source) {
        try {
            return findAction(source);
        } catch (IllegalArgumentException e) {
            return convert("undefined");
        }
    }
}
