package com.ibm.garage_catalyst.logging.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

@DisplayName("ResponseLoggingContext")
class ResponseLoggingContextTest {
    private String url = "url";
    private String statusCode = "status";
    private String statusText = "text";
    private HttpHeaders headers = new HttpHeaders();
    private Object body = "body";

    private ResponseLoggingContext classUnderTest;

    ResponseLoggingContext copyClassUnderTest() {
        return new ResponseLoggingContext(classUnderTest);
    }

    @BeforeEach
    void setup() {
        headers.add("key", "value");

        classUnderTest = new ResponseLoggingContext()
                .withUrl(url)
                .withStatusCode(statusCode)
                .withStatusText(statusText)
                .withHeaders(headers)
                .withBody(body);
    }

    @Nested
    @DisplayName("Given constructor")
    class GivenConstructor {
        @Nested
        @DisplayName("When `context` is null")
        class WhenContextIsNull {
            @Test
            @DisplayName("Then throw exception")
            void thenThrowException() {
                assertThrows(IllegalArgumentException.class, () -> {
                    new ResponseLoggingContext(null);
                });
            }
        }
    }
}

