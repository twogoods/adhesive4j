package com.github.twogoods.adhesive.agent.plugin.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/4/23
 */
public class AdhesiveSpringHttpResponse implements ClientHttpResponse {
    private int statusCode;
    private String statusText;
    private HttpHeaders headers;
    private byte[] body;

    public AdhesiveSpringHttpResponse(int statusCode, String statusText, HttpHeaders headers, byte[] body) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        return HttpStatus.valueOf(statusCode);
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return statusCode;
    }

    @Override
    public String getStatusText() throws IOException {
        return "";
    }

    @Override
    public void close() {

    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream(body);
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}