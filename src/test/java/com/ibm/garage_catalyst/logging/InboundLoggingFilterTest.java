package com.ibm.garage_catalyst.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.ibm.garage_catalyst.logging.support.RequestResponseLogger;
import com.ibm.garage_catalyst.logging.support.HttpRequestWrapper;
import com.ibm.garage_catalyst.logging.support.ResettableHttpServletRequest;

@DisplayName("LoggingFilter")
class InboundLoggingFilterTest {
    private InboundLoggingFilter classUnderTest;
    private RequestResponseLogger loggerMock;

    @BeforeEach
    void setup() {
        InboundLoggingFilter original = new InboundLoggingFilter();

        loggerMock = mock(RequestResponseLogger.class);
        ReflectionTestUtils.setField(original, "delegate", loggerMock);

        classUnderTest = spy(original);
    }

    @Nested
    @DisplayName("Given doFilter()")
    class GivenDoFilter {
        @Test
        @DisplayName("When called then it should call appropriate sequence")
        void should_log_request_doFilter_and_logResponse() throws IOException, ServletException {
            ResettableHttpServletRequest resettableRequest = mock(ResettableHttpServletRequest.class);
            doReturn(resettableRequest).when(classUnderTest).buildResettableHttpServletRequest(any());

            ContentCachingResponseWrapper cachingResponse = mock(ContentCachingResponseWrapper.class);
            doReturn(cachingResponse).when(classUnderTest).buildContentCachingResponseWrapper(any());

            doNothing().when(classUnderTest).logRequest(any());
            doNothing().when(classUnderTest).logResponse(any(), any());

            HttpServletRequest requestMock = mock(HttpServletRequest.class);
            HttpServletResponse responseMock = mock(HttpServletResponse.class);
            FilterChain chainMock = mock(FilterChain.class);

            classUnderTest.doFilter(requestMock, responseMock, chainMock);

            verify(classUnderTest).logRequest(resettableRequest);
            verify(chainMock).doFilter(resettableRequest, cachingResponse);
            verify(classUnderTest).logResponse(cachingResponse, resettableRequest);
            verify(cachingResponse).copyBodyToResponse();
        }
    }

    @Nested
    @DisplayName("Given buildResettableHttpServletRequest()")
    class GivenGetResettableHttpServletRequest {
        @Nested
        @DisplayName("When called")
        class WhenCalled {
            @Test
            @DisplayName("Then return ResettableHttpServletRequest")
            void thenReturnResettableHttpServletRequest() {
                final HttpServletRequest request = mock(HttpServletRequest.class);

                assertNotNull(classUnderTest.buildResettableHttpServletRequest(request));
            }
        }
    }

    @Nested
    @DisplayName("Given buildContentCachingResponseWrapper()")
    class GivenGetContentCachingResponseWrapper {
        @Nested
        @DisplayName("When HttpServletResponse provided")
        class WhenHttpServletResponseProvided {
            @Test
            @DisplayName("Then return ContentCachingResponseWrapper")
            void thenReturnContentCachingResponseWrapper() {
                final HttpServletResponse response = mock(HttpServletResponse.class);

                assertNotNull(classUnderTest.buildContentCachingResponseWrapper(response));
            }
        }

        @Nested
        @DisplayName("When ContentCachingResponseWrapper provided")
        class WhenContentCachingResponseWrapperProvided {
            @Test
            @DisplayName("Then return same instance")
            void thenReturnSameInstance() {
                final ContentCachingResponseWrapper response = mock(ContentCachingResponseWrapper.class);

                assertEquals(response, classUnderTest.buildContentCachingResponseWrapper(response));
            }
        }
    }

    @Nested
    @DisplayName("Given logRequest()")
    class GivenLogRequest {
        @Test
        @DisplayName("When called then call delegate.traceRequest and reset the input stream")
        void call_delegate() {

            ResettableHttpServletRequest resettableRequest = mock(ResettableHttpServletRequest.class);

            final byte[] requestPayload = "payload".getBytes();
            doReturn(requestPayload).when(classUnderTest).getRequestPayload(resettableRequest);

            classUnderTest.logRequest(resettableRequest);

            verify(loggerMock).traceRequest(
                    any(HttpRequestWrapper.class),
                    eq(requestPayload));

            verify(resettableRequest).resetInputStream();
        }
    }

    @Nested
    @DisplayName("Given getRequestPayload()")
    class GivenGetRequestPayload {
        @Nested
        @DisplayName("When request.getReader() returns a value")
        class WhenRequestGetReaderReturnsAValue {
            @Test
            @DisplayName("Then return byte array")
            void thenReturnByteArray() throws IOException {
                final String expected = "test";

                final HttpServletRequest request = mock(HttpServletRequest.class);
                final BufferedReader reader = new BufferedReader(new StringReader(expected));
                when(request.getReader()).thenReturn(reader);

                final byte[] actual = classUnderTest.getRequestPayload(request);

                assertEquals(expected, new String(actual));
            }
        }

        @Nested
        @DisplayName("When exception is thrown")
        class WhenExceptionIsThrown {
            @Test
            @DisplayName("Then return '<error>' bytes")
            @SuppressWarnings("unchecked")
            void thenReturnErrorBytes() throws IOException {
                final String expected = "<error>";

                final HttpServletRequest request = mock(HttpServletRequest.class);
                when(request.getReader()).thenThrow(IOException.class);

                final byte[] actual = classUnderTest.getRequestPayload(request);

                assertEquals(expected, new String(actual));
            }
        }
    }

    @Nested
    @DisplayName("Given logResponse()")
    class GivenLogResponse {
        @Test
        @DisplayName("When called then call delegate.traceResponse")
        void call_delegate() {

            ContentCachingResponseWrapper wrappedResponse = mock(ContentCachingResponseWrapper.class);
            ResettableHttpServletRequest resettableRequest = mock(ResettableHttpServletRequest.class);

            classUnderTest.logResponse(wrappedResponse, resettableRequest);

            verify(loggerMock).traceResponse(
                    any(ClientHttpResponse.class),
                    any(HttpRequest.class));
        }
    }
}
