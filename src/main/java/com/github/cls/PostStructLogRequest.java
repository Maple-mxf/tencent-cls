package com.github.cls;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostStructLogRequest implements ClsRequest {
    public final ImmutableList<Header> headers;
    public final String topicId;
    public final Cls.LogGroupList logGroupList;
    public final String method = "POST";
    public final String path = "/structuredlog";
    public final String region;
    public final String contentType = "application/x-protobuf";
    public final String endpoint;
    public final HttpEntity httpEntity;
    public final String url;

    public PostStructLogRequest(String endpoint, String region,
                                String secretId, String secretKey,
                                String topicId, Cls.LogGroupList logGroupList) {
        this.topicId = topicId;
        this.logGroupList = logGroupList;
        this.region = region;
        this.endpoint = endpoint;
        this.url = String.format("https://%s%s?topic_id=%s", endpoint, path, this.topicId);

        Map<String, String> hs = new HashMap<String, String>() {
            {
                this.put("Host", endpoint);
                this.put("Content-Type", contentType);
            }
        };

        String Authorization;
        try {
            Authorization = QcloudClsSignature.buildSignature(
                    secretId,
                    secretKey,
                    this.method,
                    this.path,
                    ImmutableMap.of("topic_id", this.topicId),
                    hs,
                    300000000L);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }
        hs.put("Authorization", Authorization);
        headers = hs.entrySet().stream().map(e -> new BasicHeader(e.getKey(), e.getValue())).collect(ImmutableList.toImmutableList());

        ByteArrayInputStream inputStream = new ByteArrayInputStream(logGroupList.toByteArray());
        InputStreamEntity entity = new InputStreamEntity(inputStream);
        entity.setContentType(contentType);
        this.httpEntity = entity;
    }

    @Override
    public HttpEntity getHttpEntity() {
        return this.httpEntity;
    }

    @Override
    public List<Header> getHeaders() {
        return this.headers;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

}
