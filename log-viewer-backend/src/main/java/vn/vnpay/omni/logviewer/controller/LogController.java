// log-viewer-backend/src/main/java/vn/vnpay/omni/logviewer/controller/LogController.java
package vn.vnpay.omni.logviewer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import vn.vnpay.omni.logviewer.model.LogEntry;
import vn.vnpay.omni.logviewer.model.LogFilter;
import vn.vnpay.omni.logviewer.model.ServiceInfo;
import vn.vnpay.omni.logviewer.service.LogFetcherService;
import vn.vnpay.omni.logviewer.service.LogParserService;
import vn.vnpay.omni.logviewer.service.ServiceDiscoveryService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The type Log controller.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LogController {

    private final ServiceDiscoveryService serviceDiscoveryService;
    private final LogFetcherService logFetcherService;
    private final LogParserService logParserService;

    /**
     * Gets services.
     *
     * @return the services
     */
    @GetMapping("/services")
    public Mono<ResponseEntity<List<ServiceInfo>>> getServices() {
        return serviceDiscoveryService.discoverServices()
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Error getting services", e));
    }

    /**
     * Gets logs.
     *
     * @param serviceName the service name
     * @return the logs
     */
    @GetMapping("/services/{serviceName}/logs")
    public Mono<ResponseEntity<List<LogEntry>>> getLogs(@PathVariable String serviceName) {
        return logFetcherService.fetchLogs(serviceName, "logs")
                .map(line -> logParserService.parseLine(line, serviceName))
                .filter(Objects::nonNull)
                .collectList()
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Error getting logs for service {}", serviceName, e));
    }

    /**
     * Search logs mono.
     *
     * @param filter the filter
     * @return the mono
     */
    @PostMapping("/logs/search")
    public Mono<ResponseEntity<List<LogEntry>>> searchLogs(@RequestBody LogFilter filter) {
        if (filter.getServices() == null || filter.getServices().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return Mono.fromCallable(() -> filter.getServices().stream()
                        .flatMap(serviceName -> logFetcherService.fetchLogs(serviceName, "logs")
                                .map(line -> logParserService.parseLine(line, serviceName))
                                .filter(Objects::nonNull)
                                .filter(entry -> matchesFilter(entry, filter))
                                .toStream())
                        .limit(filter.getLimit() != null ? filter.getLimit() : 1000)
                        .collect(Collectors.toList()))
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Error searching logs", e));
    }

    private boolean matchesFilter(LogEntry entry, LogFilter filter) {
        if (filter.getLogLevel() != null && entry.getLogLevel() != filter.getLogLevel()) {
            return false;
        }
        if (filter.getRequestId() != null && !filter.getRequestId().equals(entry.getRequestId())) {
            return false;
        }
        if (filter.getUsername() != null && !filter.getUsername().equals(entry.getUsername())) {
            return false;
        }
        return filter.getSearchText() == null || entry.getMessage().contains(filter.getSearchText());
    }
}