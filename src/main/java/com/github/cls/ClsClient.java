package com.github.cls;

import com.tencentcloudapi.common.Credential;

public class ClsClient {

    private final String secretId;
    private final String secretKey;
    private final Credential credential;
    private final String region;

    public ClsClient(String secretId, String secretKey, Credential credential, String region) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.credential = credential;
        this.region = region;
    }
}
