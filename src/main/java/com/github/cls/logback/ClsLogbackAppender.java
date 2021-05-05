package com.github.cls.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.github.cls.Cls;
import com.github.cls.ClsClient;
import com.github.cls.PostStructLogRequest;

import java.io.IOException;

public class ClsLogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private String secretId;

    private String secretKey;

    private String region;

    private String topicId;

    private String project;

    private String endpoint;

    private String userAgent;

    private String source;

    private String timeFormat = "yyyy-MM-dd'T'HH:mm:ssZ";

    private String timeZone = "UTC";

    private final ClsClient client;

    public ClsLogbackAppender() {
        client = new ClsClient();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        client.setEndpoint(endpoint);
    }

    @Override
    protected void append(ILoggingEvent event) {
        Cls.LogGroupList logGroupList = Cls.LogGroupList.newBuilder().addLogGroupList(
                Cls.LogGroup.newBuilder().addLogs(
                        Cls.Log.newBuilder()
                                .setTime(event.getTimeStamp())
                                .addContents(Cls.Log.Content.newBuilder().setKey("exceptionTrace").setValue(String.valueOf(getThrowableStr(event))))
                                .addContents(Cls.Log.Content.newBuilder().setKey("level").setValue(String.valueOf(event.getLevel())))
                                .addContents(Cls.Log.Content.newBuilder().setKey("threadName").setValue(event.getThreadName()))
                                .addContents(Cls.Log.Content.newBuilder().setKey("time").setValue(String.valueOf(event.getTimeStamp())))
                                .addContents(Cls.Log.Content.newBuilder().setKey("message").setValue(String.valueOf(event.getMessage())))
                )
        ).build();

        PostStructLogRequest req = client.newPostStructLogRequest(topicId, logGroupList);
        try {
            client.send(req);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }
    }

    private String getThrowableStr(ILoggingEvent event) {
        IThrowableProxy throwable = event.getThrowableProxy();
        if (throwable == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (StackTraceElementProxy s : throwable.getStackTraceElementProxyArray()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(System.getProperty("line.separator"));
            }
            sb.append(s.getSTEAsString());
        }
        return sb.toString();
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
        client.setSecretId(secretId);
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        client.setSecretKey(secretKey);
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
        client.setRegion(region);
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public ClsClient getClient() {
        return client;
    }
}
