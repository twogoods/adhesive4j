package com.github.twogoods.adhesive.agent.spy.http;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/3/10
 */
public class HttpRequest {
    public URI uri;
    public String method;
    public Map<String, List<String>> headers;
    public byte[] body;

    public HttpRequest(URI uri, String method, Map<String, List<String>> headers, byte[] body) {
        this.uri = uri;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }
}
