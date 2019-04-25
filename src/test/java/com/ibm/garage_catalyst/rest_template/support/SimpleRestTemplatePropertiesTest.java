package com.ibm.garage_catalyst.rest_template.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import com.ibm.garage_catalyst.rest_template.support.SimpleRestTemplateProperties;

@DisplayName("SimpleRestTemplateProperties")
class SimpleRestTemplatePropertiesTest {
    SimpleRestTemplateProperties classUnderTest;
    private int connectRequestTimeout = 1;
    private int connectTimeout = 2;
    private int maxTotalConnections = 3;
    private int socketTimeout = 4;
    private String providerEndpoint = "endpoint";
    private String proxyHostname = "hostname";
    private int proxyPort = 80;
    private ClientHttpRequestInterceptor loggingInterceptor;

    @BeforeEach
    void setup() {
        loggingInterceptor = mock(ClientHttpRequestInterceptor.class);

        classUnderTest = new SimpleRestTemplateProperties()
                .withConnectionRequestTimeOutInSeconds(connectRequestTimeout)
                .withConnectTimeOutInSeconds(connectTimeout)
                .withSocketTimeOutInSeconds(socketTimeout)
                .withMaxTotalConnections(maxTotalConnections)
                .withProviderEndpoint(providerEndpoint)
                .withProxyHostname(proxyHostname)
                .withProxyPort(proxyPort)
                .withLoggingInterceptor(loggingInterceptor);
    }

    @Nested
    @DisplayName("Given isProxyRequired()")
    class GivenIsProxyRequired {
        @Nested
        @DisplayName("When proxyHostname is null")
        class WhenProxyHostnameIsNull {
            @Test
            @DisplayName("Then return false")
            void thenReturnFalse() {
                classUnderTest.setProxyPort(80);
                classUnderTest.setProxyHostname(null);

                assertFalse(classUnderTest.isProxyRequired());
            }
        }

        @Nested
        @DisplayName("When proxyPort is less than or equal to 0")
        class WhenProxyPortIsLessThanOrEqualTo0 {
            @Test
            @DisplayName("Then return false")
            void thenReturnFalse() {
                classUnderTest.setProxyHostname("test");
                classUnderTest.setProxyPort(-1);

                assertFalse(classUnderTest.isProxyRequired());
            }
        }

        @Nested
        @DisplayName("When proxyHostname is not null and proxyPort is greater than 0")
        class WhenProxyHostnameIsNotNullAndProxyPortIsGreaterThan0 {
            @Test
            @DisplayName("Then return true")
            void thenReturnTrue() {
                classUnderTest.setProxyHostname("test");
                classUnderTest.setProxyPort(80);

                assertTrue(classUnderTest.isProxyRequired());
            }
        }
    }
}
