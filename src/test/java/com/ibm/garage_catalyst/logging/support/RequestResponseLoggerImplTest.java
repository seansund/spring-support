package com.ibm.garage_catalyst.logging.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("RequestResponseLogger")
class RequestResponseLoggerImplTest {

    private Logger loggerMock;
    private ObjectMapper objectMapperSpy;
    private RequestResponseLoggerImpl classUnderTest;

    @BeforeEach
    void setup() {
        loggerMock = mock(Logger.class);

        RequestResponseLoggerImpl original = new RequestResponseLoggerImpl(loggerMock);

        objectMapperSpy = spy(new ObjectMapper());
        ReflectionTestUtils.setField(original, "objectMapper", objectMapperSpy);

        classUnderTest = spy(original);
    }

    @Nested
    @DisplayName("Given traceRequest()")
    class GivenTraceRequest {
        @Test
        @DisplayName("When info logging is disabled then do nothing")
        void logging_disabled() {
            doReturn(false).when(loggerMock).isInfoEnabled();

            classUnderTest.traceRequest(null, null);

            verify(loggerMock, times(0)).info(anyString(), anyString());
        }

        @Test
        @DisplayName("When info logging is enabled then log requestContext")
        void logging_enabled() throws URISyntaxException, JsonProcessingException {
            doReturn(true).when(loggerMock).isInfoEnabled();

            final String url = "/test";
            final HttpMethod method = HttpMethod.GET;
            final HttpHeaders headers = new HttpHeaders();
            final HttpRequest request = mock(HttpRequest.class);
            doReturn(new URI(url)).when(request).getURI();
            doReturn(method).when(request).getMethod();
            doReturn(headers).when(request).getHeaders();

            final byte[] bodyBytes = new byte[] {};

            final String body = "body";
            doReturn(body).when(classUnderTest).getRequestBody(bodyBytes);

            final String contextString = "contextString";
            doReturn(contextString).when(classUnderTest).beautifyContextString(
                    new RequestLoggingContext()
                        .withUrl(url)
                        .withMethod(method.toString())
                        .withHeaders(headers)
                        .withBody(body));

            classUnderTest.traceRequest(request, bodyBytes);

            verify(loggerMock).info("{}", contextString);
        }

        @Test
        @DisplayName("When exception thrown then log warning")
        void exception() {
            doReturn(true).when(loggerMock).isInfoEnabled();

            final HttpRequest request = mock(HttpRequest.class);
            doThrow(IOException.class).when(request).getURI();

            classUnderTest.traceRequest(request, null);

            verify(loggerMock).warn(anyString(), any(IOException.class));
        }
    }

    @Nested
    @DisplayName("Given getStatusCode()")
    class GivenGetStatusCode {
        @Nested
        @DisplayName("When statusCode is available")
        class WhenStatusCodeIsAvailable {
            @Test
            @DisplayName("Then return statusCode")
            void thenReturnStatusCode() throws IOException {
                final HttpStatus expected = HttpStatus.OK;

                ClientHttpResponse response = mock(ClientHttpResponse.class);
                when(response.getStatusCode()).thenReturn(expected);

                final HttpStatus actual = classUnderTest.getStatusCode(response);

                assertEquals(expected, actual);
            }
        }

        @Nested
        @DisplayName("When getStatusCode throws exception")
        class WhenGetStatusCodeThrowsException {
            @Test
            @DisplayName("Then return null")
            @SuppressWarnings("unchecked")
            void thenReturnNull() throws IOException {
                ClientHttpResponse response = mock(ClientHttpResponse.class);
                when(response.getStatusCode()).thenThrow(IOException.class);

                assertNull(classUnderTest.getStatusCode(response));
            }
        }
    }

    @Nested
    @DisplayName("Given getStatusText()")
    class GivenGetStatusText {
        @Nested
        @DisplayName("When statusText is available")
        class WhenStatusTextIsAvailable {
            @Test
            @DisplayName("Then return statusText")
            void thenReturnStatusText() throws IOException {
                final String expected = "text";

                ClientHttpResponse response = mock(ClientHttpResponse.class);
                when(response.getStatusText()).thenReturn(expected);

                final String actual = classUnderTest.getStatusText(response);

                assertEquals(expected, actual);
            }
        }

        @Nested
        @DisplayName("When getStatusText throws exception")
        class WhenGetStatusTextThrowsException {
            @Test
            @DisplayName("Then return null")
            @SuppressWarnings("unchecked")
            void thenReturnNull() throws IOException {
                ClientHttpResponse response = mock(ClientHttpResponse.class);
                when(response.getStatusText()).thenThrow(IOException.class);

                assertNull(classUnderTest.getStatusText(response));
            }
        }
    }

    @Nested
    @DisplayName("Given getRequestBody()")
    class GivenGetRequestBody {
        @Nested
        @DisplayName("When body is null")
        class WhenBodyIsNull {
            @Test
            @DisplayName("Then return null")
            void thenReturnNull() {
                assertNull(classUnderTest.getRequestBody(null));
            }
        }

        @Nested
        @DisplayName("When body is empty")
        class WhenBodyIsEmpty {
            @Test
            @DisplayName("Then return null")
            void thenReturnNull() {
                assertNull(classUnderTest.getRequestBody(new byte[] {}));
            }
        }

        @Test
        @DisplayName("When body has a value then return value from ObjectMapper")
        void body() throws IOException {
            final byte[] body = "{}".getBytes();

            final Object expectedResult = new Object();
            doReturn(expectedResult).when(objectMapperSpy).readValue(body, Object.class);

            assertEquals(expectedResult, classUnderTest.getRequestBody(body));
        }

        @Test
        @DisplayName("When exception thrown then return string")
        void exception() throws IOException {
            final String body = "body";

            doThrow(IOException.class).when(objectMapperSpy).readValue(body, Object.class);

            assertEquals(body, classUnderTest.getRequestBody(body.getBytes()));
        }
    }

    @Nested
    @DisplayName("Given traceResponse()")
    class GivenTraceResponse {
        @Test
        @DisplayName("When debug logging is disabled then do nothing")
        void debugDisabled() {
            doReturn(false).when(loggerMock).isDebugEnabled();

            classUnderTest.traceResponse(null, null);

            verify(loggerMock, times(0)).debug(anyString(), anyString());
        }

        @Test
        @DisplayName("When debug logging is enabled then log responseContext")
        void debugEnabled() throws URISyntaxException, IOException {
            doReturn(true).when(loggerMock).isDebugEnabled();

            final String url = "/test";
            HttpRequest request = mock(HttpRequest.class);
            doReturn(new URI(url)).when(request).getURI();

            final HttpStatus statusCode = HttpStatus.OK;
            final String statusText = "status";
            final HttpHeaders headers = new HttpHeaders();
            final Object body = new Object();

            ClientHttpResponse response = mock(ClientHttpResponse.class);
            doReturn(statusCode).when(response).getStatusCode();
            doReturn(statusText).when(response).getStatusText();
            doReturn(headers).when(response).getHeaders();

            doReturn(body).when(classUnderTest).getResponseBody(response);

            final String contextString = "contextString";
            doReturn(contextString).when(classUnderTest).beautifyContextString(any());

            classUnderTest.traceResponse(response, request);

            verify(loggerMock).debug(anyString(), eq(contextString));
            verify(classUnderTest).beautifyContextString(
                    new ResponseLoggingContext()
                            .withUrl(url)
                            .withStatusCode(statusCode.toString())
                            .withStatusText(statusText)
                            .withHeaders(headers)
                            .withBody(body));
        }

        @Test
        @DisplayName("When exception is thrown then log warning")
        void exception() {
            doReturn(true).when(loggerMock).isDebugEnabled();

            HttpRequest request = mock(HttpRequest.class);
            doThrow(IOException.class).when(request).getURI();

            classUnderTest.traceResponse(null, request);

            verify(loggerMock).warn(anyString(), any(IOException.class));
        }

        @Test
        @DisplayName("When response is null then log responseContext")
        void null_response() throws URISyntaxException, JsonProcessingException {
            doReturn(true).when(loggerMock).isDebugEnabled();

            final String url = "/test";
            HttpRequest request = mock(HttpRequest.class);
            doReturn(new URI(url)).when(request).getURI();

            doReturn(null).when(classUnderTest).getResponseBody(any());

            final String contextString = "contextString";
            doReturn(contextString).when(classUnderTest).beautifyContextString(any());

            classUnderTest.traceResponse(null, request);

            verify(loggerMock).debug(anyString(), eq(contextString));
            verify(classUnderTest).beautifyContextString(
                    new ResponseLoggingContext()
                            .withUrl(url));
        }
    }

    @Nested
    @DisplayName("Given getResponseBody()")
    class GivenGetResponseBody {
        ClientHttpResponse response;

        @BeforeEach
        void setup() {
            response = mock(ClientHttpResponse.class);
        }

        @Test
        @DisplayName("When response is null then return null")
        void null_response_return_null() {
            assertNull(classUnderTest.getResponseBody(null));
        }

        @Test
        @DisplayName("When response.getBody() is null then return null")
        void null_responseBody_return_null() throws IOException {
            doReturn(null).when(response).getBody();

            assertNull(classUnderTest.getResponseBody(response));
        }

        @Test
        @DisplayName("When response.getBody() throws an exception then return null")
        void exception_return_null() throws IOException {
            doThrow(IOException.class).when(response).getBody();

            assertNull(classUnderTest.getResponseBody(response));

            verify(loggerMock).error(anyString(), anyString());
        }

        @Test
        @DisplayName("When response.getBody() returns an InputStream then return mapped object")
        void success_return_value() throws IOException {
            InputStream stream = mock(InputStream.class);
            doReturn(stream).when(response).getBody();

            final Object expectedResult = new Object();
            doReturn(expectedResult).when(objectMapperSpy).readValue(stream, Object.class);

            Object actualResult = classUnderTest.getResponseBody(response);
            assertEquals(expectedResult, actualResult);
        }
    }

    @Nested
    @DisplayName("Given beautifyContextString()")
    class GivenBeautifyContextString {

        @Test
        @DisplayName("When called then return value from objectMapper")
        void call() throws JsonProcessingException {

            final String expectedValue = "value";
            doReturn(expectedValue).when(objectMapperSpy).writeValueAsString(any());

            final LoggingContext context = mock(LoggingContext.class);
            final String actualValue = classUnderTest.beautifyContextString(context);

            assertEquals(expectedValue, actualValue);

            verify(objectMapperSpy).writeValueAsString(context);
        }

        @Test
        @DisplayName("When write value fails then throw an exception")
        void error() {

            assertThrows(JsonProcessingException.class, () -> {
                doThrow(JsonProcessingException.class).when(objectMapperSpy).writeValueAsString(any());

                final LoggingContext context = mock(LoggingContext.class);
                classUnderTest.beautifyContextString(context);
            });
        }
    }
}
