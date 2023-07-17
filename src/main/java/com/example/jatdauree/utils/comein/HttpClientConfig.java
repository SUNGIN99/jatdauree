package com.example.jatdauree.utils.comein;


import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    private static final int MAX_CONNECTION_PER_ROUTE = 20;
    private static final int MAX_CONNECTION_TOTAL = 200;
    private static final int CONNECTION_TIMEOUT = 10;
    private static final int SOCKET_TIMEOUT = 5;
    private static final int CONNECTION_REQUEST_TIMEOUT = 5;

    @Bean
    public CloseableHttpClient closeableHttpClient() {
        CloseableHttpClient closeableHttpClient =
                HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager()).build();

        return closeableHttpClient;

    }

    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTION_PER_ROUTE);
        connectionManager.setMaxTotal(MAX_CONNECTION_TOTAL);
        return connectionManager;
    }

    @Bean
    public RequestConfig requestConfig() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIMEOUT * 1000)
                .setConnectTimeout(CONNECTION_TIMEOUT * 1000)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT * 1000)
                .build();

        return requestConfig;
    }
}
