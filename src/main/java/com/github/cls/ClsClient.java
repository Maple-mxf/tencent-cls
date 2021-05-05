package com.github.cls;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ClsClient {

    private  String secretId;
    private  String secretKey;
    private  String region;
    private  String endpoint;

    public ClsClient() {
    }

    public ClsClient(String secretId, String secretKey, String region, String endpoint) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.region = region;
        this.endpoint = endpoint;
    }

    public PostStructLogRequest newPostStructLogRequest(String topicId, Cls.LogGroupList logGroupList) {
        return new PostStructLogRequest(endpoint, region, secretId, secretKey, topicId, logGroupList);
    }

    public String send(ClsRequest req) throws IOException {
        String result = null;
        switch (req.getMethod()) {
            case "POST": {
                HttpPost post = new HttpPost(req.getUrl());
                req.getHeaders().forEach(post::addHeader);
                post.setEntity(req.getHttpEntity());
                try (CloseableHttpClient httpClient = HttpClients.createDefault();
                     CloseableHttpResponse response = httpClient.execute(post)) {
                    result = EntityUtils.toString(response.getEntity());
                }
                break;
            }
            case "GET": {
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + req.getMethod());
        }
        return result;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
