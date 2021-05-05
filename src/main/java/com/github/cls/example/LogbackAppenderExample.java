package com.github.cls.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackAppenderExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackAppenderExample.class);

    public static void main(String[] args) {
        LOGGER.trace("cls logback trace log");
        LOGGER.debug("cls logback debug log");
        LOGGER.info("cls logback info log");
        LOGGER.warn("cls logback warn log");
        LOGGER.error("cls logback error log", new RuntimeException("Runtime Exception"));
    }
}
