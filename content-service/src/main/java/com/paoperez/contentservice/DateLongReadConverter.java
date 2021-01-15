package com.paoperez.contentservice;

import java.util.Date;
import org.springframework.core.convert.converter.Converter;

class DateLongReadConverter implements Converter<Date, Long> {
    @Override
    public Long convert(Date date) {
        return date.toInstant().toEpochMilli();
    }
}
