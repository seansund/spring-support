package com.ibm.garage_catalyst.logging.support;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.springframework.http.HttpHeaders;

import lombok.Data;

@JsonRootName(value = "RequestLoggingContext")
@JsonPropertyOrder({ "url", "method", "headers", "body" })
@Data
public class RequestLoggingContext implements LoggingContext {
    private String url;
    private String method;
    private HttpHeaders headers;
    private Object body;

    public RequestLoggingContext() {
        super();
    }

    public RequestLoggingContext(RequestLoggingContext context) {
        super();

        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

        setUrl(context.getUrl());
        setMethod(context.getMethod());
        setHeaders(context.getHeaders());
        setBody(context.getBody());
    }

    public RequestLoggingContext withUrl(String url) {
        this.setUrl(url);
        return this;
    }

    public RequestLoggingContext withMethod(String method) {
        this.setMethod(method);
        return this;
    }

    public RequestLoggingContext withHeaders(HttpHeaders headers) {
        this.setHeaders(headers);
        return this;
    }

    public RequestLoggingContext withBody(Object body) {
        this.setBody(body);
        return this;
    }
}
