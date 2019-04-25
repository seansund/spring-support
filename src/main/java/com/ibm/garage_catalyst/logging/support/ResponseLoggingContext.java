package com.ibm.garage_catalyst.logging.support;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.springframework.http.HttpHeaders;

import lombok.Data;

@JsonRootName(value = "ResponseLoggingContext")
@JsonPropertyOrder({ "url", "statusCode", "statusText", "headers", "body" })
@Data
public class ResponseLoggingContext implements LoggingContext {
    private String url;
    private String statusCode;
    private String statusText;
    private HttpHeaders headers;
    private Object body;

    public ResponseLoggingContext() {
        super();
    }

    public ResponseLoggingContext(ResponseLoggingContext context) {
        super();

        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

        setUrl(context.getUrl());
        setStatusCode(context.getStatusCode());
        setStatusText(context.getStatusText());
        setHeaders(context.getHeaders());
        setBody(context.getBody());
    }

    public ResponseLoggingContext withStatusCode(String statusCode) {
        this.setStatusCode(statusCode);
        return this;
    }

    public ResponseLoggingContext withStatusText(String statusText) {
        this.setStatusText(statusText);
        return this;
    }

    public ResponseLoggingContext withHeaders(HttpHeaders headers) {
        this.setHeaders(headers);
        return this;
    }

    public ResponseLoggingContext withBody(Object body) {
        this.setBody(body);
        return this;
    }

    public ResponseLoggingContext withUrl(String url) {
        this.setUrl(url);
        return this;
    }
}
