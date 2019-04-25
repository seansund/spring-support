package com.ibm.garage_catalyst.logging.support;

import static junit.framework.TestCase.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

@DisplayName("HttpRequestWrapper")
class HttpRequestWrapperTest {
    private HttpRequestWrapper classUnderTest;
    private HttpServletRequest requestMock;

    @BeforeEach
    void setup() {
        requestMock = mock(HttpServletRequest.class);
        HttpRequestWrapper original = new HttpRequestWrapper(requestMock);

        classUnderTest = spy(original);
    }

    @Nested
    @DisplayName("Given getMethodValue()")
    class GivenGetMethodValue {
        @Nested
        @DisplayName("When called")
        class WhenCalled {
            @Test
            @DisplayName("Then return request.getMethod()")
            void thenReturnRequestGetMethod() {
                final String expected = "method";
                when(requestMock.getMethod()).thenReturn(expected);

                final String actual = classUnderTest.getMethodValue();

                assertEquals(expected, actual);
            }
        }
    }

    @Nested
    @DisplayName("Given getURI()")
    class GivenGetUri {
        @Nested
        @DisplayName("When called successfully")
        class WhenCalledSuccessfully {
            @Test
            @DisplayName("Then return URI")
            void thenReturnUri() throws URISyntaxException {
                final String requestPath = "/request";
                final String requestUri = "/requestURI";
                final Map<String, String[]> parameterMap = new HashMap<>();

                when(requestMock.getRequestURI()).thenReturn(requestUri);
                when(requestMock.getParameterMap()).thenReturn(parameterMap);

                doReturn(requestPath).when(classUnderTest).getRequestPath(requestUri, parameterMap);

                final URI actual = classUnderTest.getURI();

                assertEquals(new URI(requestPath), actual);
            }
        }

        @Nested
        @DisplayName("When exception thrown")
        class WhenExceptionThrown {
            @Test
            @DisplayName("Then return null")
            @SuppressWarnings("unchecked")
            void thenReturnNull() {
                when(requestMock.getRequestURI()).thenThrow(URISyntaxException.class);

                assertNull(classUnderTest.getURI());
            }
        }
    }

    @Nested
    @DisplayName("Given getRequestPath()")
    class GivenGetRequestPath {
        @Test
        @DisplayName("When requestUri is null then return '/'")
        void null_request_uri_return_forward_slash() {
            assertEquals("/", classUnderTest.getRequestPath(null, new HashMap<>()));
        }

        @Test
        @DisplayName("When requestUri is '/test' and parameterMap is null then return '/test'")
        void null_parameterMap_returns_requestUri() {
            final String requestUri = "/test";

            assertEquals(requestUri, classUnderTest.getRequestPath(requestUri, null));
        }

        @Test
        @DisplayName("When requestUri is '/test' and parameterMap is 'key'=['value'] then return '/test?key=value'")
        void single_value_param() {
            final String requestUri = "/test";
            final String paramName = "key";
            final String paramValue = "value";

            Map<String, String[]> parameterMap = new HashMap<>();
            parameterMap.put(paramName, new String[] {paramValue});

            assertEquals(
                    requestUri + "?" + paramName + "=" + paramValue,
                    classUnderTest.getRequestPath(requestUri, parameterMap));
        }

        @Test
        @DisplayName("When requestUri is '/test' and parameterMap is 'key'=['value1','value2'] "
                + "then return '/test?key=value1&key=value2'")
        void multiple_value_param() {
            final String requestUri = "/test";
            final String paramName = "key";
            final String paramValue1 = "value1";
            final String paramValue2 = "value2";

            Map<String, String[]> parameterMap = new HashMap<>();
            parameterMap.put(paramName, new String[] {paramValue1, paramValue2});

            assertEquals(
                    requestUri + "?" + paramName + "=" + paramValue1 + "&" + paramName + "=" + paramValue2,
                    classUnderTest.getRequestPath(requestUri, parameterMap));
        }

        @Test
        @DisplayName("When requestUri is '/test' and parameterMap is 'key1'=['value1'],'key2'=['value2'] "
                + "then return '/test?key1=value1&key2=value2'")
        void multiple_params() {
            final String requestUri = "/test";
            final String paramName1 = "key1";
            final String paramName2 = "key2";
            final String paramValue1 = "value1";
            final String paramValue2 = "value2";

            Map<String, String[]> parameterMap = new HashMap<>();
            parameterMap.put(paramName1, new String[] {paramValue1});
            parameterMap.put(paramName2, new String[] {paramValue2});

            assertEquals(
                    requestUri + "?" + paramName1 + "=" + paramValue1 + "&" + paramName2 + "=" + paramValue2,
                    classUnderTest.getRequestPath(requestUri, parameterMap));
        }
    }

    @Nested
    @DisplayName("Given getHeaders()")
    class GivenGetHeaders {
        @Nested
        @DisplayName("When no headerNames")
        class WhenNoHeaderNames {
            @Test
            @DisplayName("Then return empty HttpHeaders")
            @SuppressWarnings("unchecked")
            void thenReturnEmptyHttpHeaders() {
                final Enumeration headerNames = mock(Enumeration.class);
                when(headerNames.hasMoreElements()).thenReturn(false);

                when(requestMock.getHeaderNames()).thenReturn(headerNames);

                final HttpHeaders actual = classUnderTest.getHeaders();

                assertEquals(new HttpHeaders(), actual);
            }
        }

        @Nested
        @DisplayName("When one header name")
        class WhenOneHeaderName {
            @Test
            @DisplayName("Then return HttpHeaders with single value")
            @SuppressWarnings("unchecked")
            void thenReturnHttpHeadersWithSingleValue() {
                final Enumeration headerNames = mock(Enumeration.class);
                when(headerNames.hasMoreElements()).thenReturn(true, false);

                final String headerName = "header";
                when(headerNames.nextElement()).thenReturn(headerName);

                final Enumeration headers = mock(Enumeration.class);
                when(headers.hasMoreElements()).thenReturn(true, true, false);

                final String value1 = "value1";
                final String value2 = "value2";
                when(headers.nextElement()).thenReturn(value1, value2);

                when(requestMock.getHeaderNames()).thenReturn(headerNames);
                when(requestMock.getHeaders(headerName)).thenReturn(headers);

                final HttpHeaders actual = classUnderTest.getHeaders();

                assertTrue(actual.containsKey(headerName));
                assertEquals(Arrays.asList(value1, value2), actual.get(headerName));
            }
        }
    }
}
