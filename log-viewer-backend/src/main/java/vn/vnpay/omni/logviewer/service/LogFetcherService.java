// log-viewer-backend/src/main/java/vn/vnpay/omni/logviewer/service/LogFetcherService.java
package vn.vnpay.omni.logviewer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogFetcherService {

    private final WebClient webClient;

    public Flux<String> fetchLogs(String serviceName, String logFileName) {
        String url = serviceName + "/" + logFileName;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(byte[].class)
                .flatMapMany(bytes -> {
                    String content = new String(bytes, StandardCharsets.UTF_8);
                    String[] lines = content.split("\n");
                    return Flux.fromArray(lines);
                })
                .doOnError(e -> log.error("Error fetching logs from {}", url, e))
                .onErrorResume(e -> Flux.empty());
    }

    public Mono<String> fetchLogsAsString(String serviceName) {
        String url = serviceName + "/logs";

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("Error fetching logs from service {}", serviceName, e))
                .onErrorReturn("");
    }
}