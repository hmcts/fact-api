package uk.gov.hmcts.dts.fact.util;

import org.springframework.core.convert.converter.Converter;

public class ActionConverter implements Converter<String, Action> {
    @Override
    public Action convert(String source) {
        try{
            return Action.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Action.UNDEFINED;
        }
    }
}
