// log-viewer-backend/src/main/java/vn/vnpay/omni/logviewer/service/LogStreamService.java
package vn.vnpay.omni.logviewer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.vnpay.omni.logviewer.model.LogEntry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogStreamService {

    private final SimpMessagingTemplate messagingTemplate;
    private final LogFetcherService logFetcherService;
    private final LogParserService logParserService;

    private final Map<String, Boolean> activeStreams = new ConcurrentHashMap<>();

    public void startStream(String serviceName) {
        activeStreams.put(serviceName, true);
        log.info("Started log stream for service: {}", serviceName);
    }

    public void stopStream(String serviceName) {
        activeStreams.remove(serviceName);
        log.info("Stopped log stream for service: {}", serviceName);
    }

    @Scheduled(fixedDelayString = "${log-source.poll-interval:3000}")
    public void streamLogs() {
        activeStreams.keySet().forEach(serviceName -> {
            logFetcherService.fetchLogs(serviceName, "logs")
                    .map(line -> logParserService.parseLine(line, serviceName))
                    .filter(logEntry -> logEntry != null)
                    .subscribe(
                            logEntry -> sendLogToClient(serviceName, logEntry),
                            error -> log.error("Error streaming logs for {}", serviceName, error)
                    );
        });
    }

    private void sendLogToClient(String serviceName, LogEntry logEntry) {
        messagingTemplate.convertAndSend("/topic/logs/" + serviceName, logEntry);
    }
}