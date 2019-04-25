package com.ibm.garage_catalyst.logging.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingResponseWrapper;

@DisplayName("ClientHttpResponseWrapper")
class ClientHttpResponseWrapperTest {
    private ContentCachingResponseWrapper responseMock;
    private ClientHttpResponseWrapper classUnderTest;
    private int status;

    @BeforeEach
    void setup() {
        status = 200;

        responseMock = mock(ContentCachingResponseWrapper.class);
        when(responseMock.getStatus()).thenReturn(status);

        classUnderTest = spy(new ClientHttpResponseWrapper(responseMock));
    }

    @Nested
    @DisplayName("Given getStatusCode()")
    class GivenGetStatusCode {
        @Nested
        @DisplayName("When called")
        class WhenCalled {
            @Test
            @DisplayName("Then return response.getStatus()")
            void thenReturnResponseGetStatus() throws IOException {

                final HttpStatus actual = classUnderTest.getStatusCode();

                assertEquals(HttpStatus.OK, actual);
            }
        }
    }

    @Nested
    @DisplayName("Given getRawStatusCode()")
    class GivenGetRawStatusCode {
        @Nested
        @DisplayName("When called")
        class WhenCalled {
            @Test
            @DisplayName("Then return response.getStatus()")
            void thenReturnResponseGetStatus() throws IOException {

                assertEquals(status, classUnderTest.getRawStatusCode());
            }
        }
    }

    @Nested
    @DisplayName("Given getStatusText()")
    class GivenGetStatusText {
        @Nested
        @DisplayName("When called")
        class WhenCalled {
            @Test
            @DisplayName("Then return getStatusCode().getReasonPhrase()")
            void thenReturnGetStatusCodeGetReasonPhrase() throws IOException {
                final HttpStatus status = HttpStatus.OK;

                doReturn(status).when(classUnderTest).getStatusCode();

                assertEquals(status.getReasonPhrase(), classUnderTest.getStatusText());
            }
        }
    }

    @Nested
    @DisplayName("Given close()")
    class GivenClose {
        @Nested
        @DisplayName("When called")
        class WhenCalled {
            @Test
            @DisplayName("Then throw UnsupportedOperationException")
            void thenThrowUnsupportedOperationException() {
                assertThrows(UnsupportedOperationException.class, () -> classUnderTest.close());
            }
        }
    }

    @Nested
    @DisplayName("Given getBody()")
    class GivenGetBody {
        @Nested
        @DisplayName("When called")
        class WhenCalled {
            @Test
            @DisplayName("Then return ByteArrayOutputStream")
            void thenReturnByteArrayOutputStream() throws IOException {
                final String expected = "test string";
                byte[] bytes = expected.getBytes();
                when(responseMock.getContentAsByteArray()).thenReturn(bytes);

                final InputStream inputStream = classUnderTest.getBody();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                assertEquals(expected, reader.readLine());
            }
        }
    }

    @Nested
    @DisplayName("Given getHeaders()")
    class GivenGetHeaders {
        @Nested
        @DisplayName("When headerNames empty")
        class WhenHeaderNamesEmpty {
            @Test
            @DisplayName("Then return empty HttpHeaders")
            void thenReturnEmptyHttpHeaders() {
                final List<String> headerNames = new ArrayList<>();

                when(responseMock.getHeaderNames()).thenReturn(headerNames);

                assertEquals(new HttpHeaders(), classUnderTest.getHeaders());
            }
        }

        @Nested
        @DisplayName("When headerNames has values")
        class WhenHeaderNamesHasValues {
            @Test
            @DisplayName("Then return HttpHeaders populated with headers")
            void thenReturnHttpHeadersPopulatedWithHeaders() {
                final String headerName = "headerName";
                final String value1 = "value1";
                final String value2 = "value2";
                final List<String> headerNames = Collections.singletonList(headerName);
                final List<String> headers = Arrays.asList(value1, value2);

                when(responseMock.getHeaderNames()).thenReturn(headerNames);
                when(responseMock.getHeaders(headerName)).thenReturn(headers);

                final HttpHeaders httpHeaders = classUnderTest.getHeaders();

                assertTrue(httpHeaders.containsKey(headerName));
                assertEquals(headers, httpHeaders.get(headerName));
            }
        }
    }
}
