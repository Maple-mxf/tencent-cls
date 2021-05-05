package com.github.cls.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class ClsLogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private String endpoint;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    protected void append(ILoggingEvent event) {
        System.err.println(endpoint);
        System.err.println(event.getLevel());
    }
}
