package com.github.cls;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.util.List;

public interface ClsRequest {

    HttpEntity getHttpEntity();

    List<Header> getHeaders();

    String getUrl();

    String getMethod();
}
