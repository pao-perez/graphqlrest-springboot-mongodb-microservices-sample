package com.paoperez.contentservice;

import java.time.Instant;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

public class DateLongWriteConverter implements Converter<Long, Date> {
    @Override
    public Date convert(Long epochMilli) {
        return Date.from(Instant.ofEpochMilli(epochMilli));
    }
}
