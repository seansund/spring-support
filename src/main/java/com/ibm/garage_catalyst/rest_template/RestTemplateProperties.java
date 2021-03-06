package com.ibm.garage_catalyst.rest_template;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.StringUtils;

public interface RestTemplateProperties<T extends RestTemplateProperties<T>> {
    static final int DEFAULT_CONNECTIONS = 3;
    static final int DEFAULT_TIMEOUT = 100;

    String getProviderEndpoint();

    void setProviderEndpoint(String providerEndpoint);

    @SuppressWarnings("unchecked")
    default T withProviderEndpoint(String providerEndpoint) {
        this.setProviderEndpoint(providerEndpoint);
        return (T) this;
    }

    int getMaxTotalConnections();

    void setMaxTotalConnections(int maxTotalConnections);

    @SuppressWarnings("unchecked")
    default T withMaxTotalConnections(int maxTotalConnections) {
        this.setMaxTotalConnections(maxTotalConnections);
        return (T) this;
    }

    int getConnectTimeOutInSeconds();

    void setConnectTimeOutInSeconds(int connectTimeOutInSeconds);

    @SuppressWarnings("unchecked")
    default T withConnectTimeOutInSeconds(int connectTimeOutInSeconds) {
        this.setConnectTimeOutInSeconds(connectTimeOutInSeconds);
        return (T) this;
    }

    int getSocketTimeOutInSeconds();

    void setSocketTimeOutInSeconds(int socketTimeOutInSeconds);

    @SuppressWarnings("unchecked")
    default T withSocketTimeOutInSeconds(int socketTimeOutInSeconds) {
        this.setSocketTimeOutInSeconds(socketTimeOutInSeconds);
        return (T) this;
    }

    int getConnectionRequestTimeOutInSeconds();

    void setConnectionRequestTimeOutInSeconds(int connectionRequestTimeOutInSeconds);

    @SuppressWarnings("unchecked")
    default T withConnectionRequestTimeOutInSeconds(int connectionRequestTimeOutInSeconds) {
        this.setConnectionRequestTimeOutInSeconds(connectionRequestTimeOutInSeconds);
        return (T) this;
    }

    default boolean isProxyRequired() {
        return StringUtils.hasText(getProxyHostname()) && getProxyPort() > 0;
    }

    String getProxyHostname();

    void setProxyHostname(String proxyHostname);

    @SuppressWarnings("unchecked")
    default T withProxyHostname(String proxyHostname) {
        this.setProxyHostname(proxyHostname);
        return (T) this;
    }

    int getProxyPort();

    void setProxyPort(int proxyPort);

    @SuppressWarnings("unchecked")
    default T withProxyPort(int proxyPort) {
        this.setProxyPort(proxyPort);
        return (T) this;
    }

    ClientHttpRequestInterceptor getLoggingInterceptor();

    void setLoggingInterceptor(ClientHttpRequestInterceptor loggingInterceptor);

    @SuppressWarnings("unchecked")
    default T withLoggingInterceptor(ClientHttpRequestInterceptor loggingInterceptor) {
        this.setLoggingInterceptor(loggingInterceptor);
        return (T) this;
    }
}
