package com.ibm.garage_catalyst.logging.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

@DisplayName("RequestLoggingContext")
class RequestLoggingContextTest {
    private String url = "url";
    private String method = "method";
    private HttpHeaders headers = new HttpHeaders();
    private Object body = "body";
    private RequestLoggingContext classUnderTest;

    RequestLoggingContext copyClassUnderTest() {
        return new RequestLoggingContext(classUnderTest);
    }

    @BeforeEach
    void setup() {
        headers.add("test", "value");

        classUnderTest = new RequestLoggingContext()
                .withUrl(url)
                .withMethod(method)
                .withHeaders(headers)
                .withBody(body);
    }

    @Nested
    @DisplayName("Given constructor()")
    class GivenConstructor {
        @Nested
        @DisplayName("When `context` is null")
        class WhenContextIsNull {
            @Test
            @DisplayName("Then throw IllegalArgumentException")
            void thenThrowIllegalArgumentException() {
                assertThrows(IllegalArgumentException.class, () -> new RequestLoggingContext(null));
            }
        }
    }
}
