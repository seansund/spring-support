package com.ibm.garage_catalyst.rest_template.support;

import org.springframework.http.client.ClientHttpRequestInterceptor;

import com.ibm.garage_catalyst.rest_template.RestTemplateProperties;
import lombok.Data;

@Data
public class SimpleRestTemplateProperties implements RestTemplateProperties<SimpleRestTemplateProperties> {

    private String providerEndpoint;
    private int maxTotalConnections = DEFAULT_CONNECTIONS;
    private int connectTimeOutInSeconds = DEFAULT_TIMEOUT;
    private int socketTimeOutInSeconds = DEFAULT_TIMEOUT;
    private int connectionRequestTimeOutInSeconds = DEFAULT_TIMEOUT;
    private String proxyHostname;
    private int proxyPort = 0;
    private ClientHttpRequestInterceptor loggingInterceptor;

    public SimpleRestTemplateProperties() {
        super();
    }

    public SimpleRestTemplateProperties(RestTemplateProperties properties) {
        super();

        setProviderEndpoint(properties.getProviderEndpoint());
        setMaxTotalConnections(properties.getMaxTotalConnections());
        setConnectTimeOutInSeconds(properties.getConnectTimeOutInSeconds());
        setSocketTimeOutInSeconds(properties.getSocketTimeOutInSeconds());
        setConnectionRequestTimeOutInSeconds(properties.getConnectionRequestTimeOutInSeconds());
        setProxyHostname(properties.getProxyHostname());
        setProxyPort(properties.getProxyPort());
        setLoggingInterceptor(properties.getLoggingInterceptor());
    }
}
