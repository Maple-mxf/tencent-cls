package com.github.cls.log4j;

import com.github.cls.Cls;
import com.github.cls.ClsClient;
import com.github.cls.PostStructLogRequest;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.io.IOException;

public class ClsLog4jAppender extends AppenderSkeleton {

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

    public ClsLog4jAppender() {
        this.client = new ClsClient();
    }

    @Override
    protected void append(LoggingEvent event) {
        Cls.LogGroupList logGroupList = Cls.LogGroupList.newBuilder().addLogGroupList(
                Cls.LogGroup.newBuilder().addLogs(
                        Cls.Log.newBuilder()
                                .setTime(event.timeStamp)
                                .addContents(Cls.Log.Content.newBuilder().setKey("exceptionTrace").setValue(String.valueOf(getThrowableStr(event))))
                                .addContents(Cls.Log.Content.newBuilder().setKey("level").setValue(String.valueOf(event.getLevel())))
                                .addContents(Cls.Log.Content.newBuilder().setKey("threadName").setValue(event.getThreadName()))
                                .addContents(Cls.Log.Content.newBuilder().setKey("time").setValue(String.valueOf(event.timeStamp)))
                                .addContents(Cls.Log.Content.newBuilder().setKey("message").setValue(String.valueOf(event.getMessage())))
                )
        ).build();
        try {
            PostStructLogRequest req = client.newPostStructLogRequest(topicId, logGroupList);
            String result = client.send(req);
            System.err.println(result);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
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

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
        this.client.setSecretId(secretId);
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        this.client.setSecretKey(secretKey);
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
        this.client.setRegion(region);
    }

    public String getTopicId() {
        return topicId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        this.client.setEndpoint(endpoint);
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

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}
