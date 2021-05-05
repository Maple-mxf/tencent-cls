package com.github.cls.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackAppenderExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackAppenderExample.class);

    public static void main(String[] args) {
        LOGGER.debug("I am lockback log");
    }
}
