package com.github.twogoods.adhesive.agent.spy.http;

import java.util.List;
import java.util.Map;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/3/10
 */
public class HttpResponse {
    public Map<String, List<String>> headers;
    public int code;
    public byte[] body;

    public HttpResponse() {
    }

    public HttpResponse(int code, byte[] body) {
        this.code = code;
        this.body = body;
    }

    public HttpResponse(Map<String, List<String>> headers, int code, byte[] body) {
        this.headers = headers;
        this.code = code;
        this.body = body;
    }
}
