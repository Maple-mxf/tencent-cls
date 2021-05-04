package com.github.cls.log4j;

import com.tencentcloudapi.common.Credential;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

public class ClsLogAppender extends AppenderSkeleton {

    private final String secretId;
    private final String secretKey;
    private final Credential credential;
    private final String region;

    public ClsLogAppender(String secretId, String secretKey, String region) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.region = region;
        this.credential = new Credential(secretId, secretKey);
    }

    protected void append(LoggingEvent event) {
        String exTrace = getThrowableStr(event);

    }

    public void close() {

    }

    public boolean requiresLayout() {
        return false;
    }

    private String getThrowableStr(LoggingEvent event) {
        ThrowableInformation throwable = event.getThrowableInformation();
        if (throwable == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String s : throwable.getThrowableStrRep()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(System.getProperty("line.separator"));
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
