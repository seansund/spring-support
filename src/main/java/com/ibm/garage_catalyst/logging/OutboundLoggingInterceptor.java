package com.ibm.garage_catalyst.logging;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.ibm.garage_catalyst.logging.support.RequestResponseLogger;
import com.ibm.garage_catalyst.logging.support.RequestResponseLoggerImpl;

public class OutboundLoggingInterceptor implements ClientHttpRequestInterceptor {
    private static Logger logger = LoggerFactory.getLogger(OutboundLoggingInterceptor.class);

    private final RequestResponseLogger delegate;

    public OutboundLoggingInterceptor() {
        this.delegate = new RequestResponseLoggerImpl(logger);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        ClientHttpResponse clientHttpResponse = null;
        try {
            delegate.traceRequest(request, body);
            clientHttpResponse = execution.execute(request, body);
        } finally {
            delegate.traceResponse(clientHttpResponse, request);
        }

        return clientHttpResponse;
    }
}
