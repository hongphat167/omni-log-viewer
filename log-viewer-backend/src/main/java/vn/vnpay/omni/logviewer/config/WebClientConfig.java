package vn.vnpay.omni.logviewer.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${log-source.base-url}")
    private String logSourceBaseUrl;

    @Value("${log-source.ssl-verify:false}")
    private boolean sslVerify;

    @Bean
    public WebClient webClient() throws SSLException {
        HttpClient httpClient;
        
        if (!sslVerify) {
            log.warn("SSL certificate verification is DISABLED");
            
            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            
            httpClient = HttpClient.create()
                    .secure(sslSpec -> sslSpec.sslContext(sslContext));
        } else {
            httpClient = HttpClient.create();
        }
        
        return WebClient.builder()
                .baseUrl(logSourceBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}