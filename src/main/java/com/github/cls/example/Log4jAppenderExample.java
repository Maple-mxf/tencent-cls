package com.github.cls.example;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Log4jAppenderExample {
    private static final Logger LOGGER = LogManager.getLogger(Log4jAppenderExample.class);
    public static void main(String[] args) {
        LOGGER.trace("cls log4j trace log");
        LOGGER.debug("cls log4j debug log");
        LOGGER.info("cls log4j info log");
        LOGGER.warn("cls log4j warn log");
        LOGGER.error("cls log4j error log", new RuntimeException("Runtime Exception"));
    }
}
