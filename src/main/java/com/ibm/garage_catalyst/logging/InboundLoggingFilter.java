package com.ibm.garage_catalyst.logging;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.ibm.garage_catalyst.logging.support.ClientHttpResponseWrapper;
import com.ibm.garage_catalyst.logging.support.HttpRequestWrapper;
import com.ibm.garage_catalyst.logging.support.ReaderHelper;
import com.ibm.garage_catalyst.logging.support.RequestResponseLogger;
import com.ibm.garage_catalyst.logging.support.RequestResponseLoggerImpl;
import com.ibm.garage_catalyst.logging.support.ResettableHttpServletRequest;
import com.ibm.garage_catalyst.logging.support.SimpleFilter;

@Component
public class InboundLoggingFilter implements SimpleFilter {
    private static final Logger logger = LoggerFactory.getLogger(InboundLoggingFilter.class);

    private final RequestResponseLogger delegate;

    public InboundLoggingFilter() {
        this.delegate = new RequestResponseLoggerImpl(logger);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ResettableHttpServletRequest resettableRequest = buildResettableHttpServletRequest(request);
        ContentCachingResponseWrapper cachingResponse = buildContentCachingResponseWrapper(response);

        try {
            logRequest(resettableRequest);

            chain.doFilter(resettableRequest, cachingResponse);

            logResponse(cachingResponse, resettableRequest);
        } finally {
            cachingResponse.copyBodyToResponse();
        }
    }

    protected ResettableHttpServletRequest buildResettableHttpServletRequest(ServletRequest request) {
        return new ResettableHttpServletRequest(request);
    }

    protected ContentCachingResponseWrapper buildContentCachingResponseWrapper(ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        return (response instanceof ContentCachingResponseWrapper)
                ? (ContentCachingResponseWrapper) response
                : new ContentCachingResponseWrapper(httpServletResponse);
    }

    protected void logRequest(ResettableHttpServletRequest resettableRequest) {
        delegate.traceRequest(new HttpRequestWrapper(resettableRequest), getRequestPayload(resettableRequest));

        resettableRequest.resetInputStream();
    }

    protected byte[] getRequestPayload(final HttpServletRequest request) {
        try {
            return ReaderHelper.readerToByteArray(request.getReader());
        } catch (Throwable ex) {
            return "<error>".getBytes();
        }
    }

    protected void logResponse(ContentCachingResponseWrapper wrappedResponse, HttpServletRequest request) {
        delegate.traceResponse(new ClientHttpResponseWrapper(wrappedResponse), new HttpRequestWrapper(request));
    }
}
