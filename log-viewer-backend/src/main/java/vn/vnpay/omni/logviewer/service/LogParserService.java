// log-viewer-backend/src/main/java/vn/vnpay/omni/logviewer/service/LogParserService.java
package vn.vnpay.omni.logviewer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.vnpay.omni.logviewer.model.LogEntry;
import vn.vnpay.omni.logviewer.model.LogLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Log parser service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogParserService {

    private final ObjectMapper objectMapper;

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "\\[([^\\]]+)\\]\\[([^\\]]+)\\]\\s*-\\s*\\[([^\\]]*)\\]\\s*\\[([^\\]]*)\\]\\s*\\[([^\\]]*)\\]\\s*(.+)"
    );

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Parse line log entry.
     *
     * @param line        the line
     * @param serviceName the service name
     * @return the log entry
     */
    public LogEntry parseLine(String line, String serviceName) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            Matcher matcher = LOG_PATTERN.matcher(line);

            if (matcher.find()) {
                String version = matcher.group(1);
                String timestampStr = matcher.group(2);
                String requestId = matcher.group(3);
                String sessionId = matcher.group(4);
                String username = matcher.group(5);
                String remaining = matcher.group(6);

                String path = null;
                String content = remaining;

                if (remaining.startsWith("[") && remaining.contains("]")) {
                    int closeBracket = remaining.indexOf("]");
                    path = remaining.substring(1, closeBracket);
                    content = remaining.substring(closeBracket + 1).trim();
                }

                LocalDateTime timestamp = parseTimestamp(timestampStr);
                LogLevel logLevel = detectLogLevel(content);

                String jsonContent = null;
                String message = content;

                if (content.startsWith("{")) {
                    jsonContent = content;
                    try {
                        objectMapper.readTree(jsonContent);
                    } catch (Exception e) {
                        jsonContent = null;
                    }
                }

                return LogEntry.builder()
                        .id(UUID.randomUUID().toString())
                        .version(version)
                        .timestamp(timestamp)
                        .requestId(requestId.isEmpty() ? null : requestId)
                        .sessionId(sessionId.isEmpty() ? null : sessionId)
                        .username(username.isEmpty() ? null : username)
                        .path(path)
                        .logLevel(logLevel)
                        .message(message)
                        .jsonContent(jsonContent)
                        .serviceName(serviceName)
                        .rawLog(line)
                        .build();
            }

            return LogEntry.builder()
                    .id(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .logLevel(LogLevel.INFO)
                    .message(line)
                    .serviceName(serviceName)
                    .rawLog(line)
                    .build();

        } catch (Exception e) {
            log.error("Error parsing log line", e);
            return null;
        }
    }

    private LocalDateTime parseTimestamp(String timestampStr) {
        try {
            return LocalDateTime.parse(timestampStr, TIMESTAMP_FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    private LogLevel detectLogLevel(String content) {
        String upperContent = content.toUpperCase();

        if (upperContent.contains("ERROR") || upperContent.contains("\"level\":\"ERROR\"")) {
            return LogLevel.ERROR;
        } else if (upperContent.contains("WARN") || upperContent.contains("\"level\":\"WARN\"")) {
            return LogLevel.WARN;
        } else if (upperContent.contains("DEBUG") || upperContent.contains("\"level\":\"DEBUG\"")) {
            return LogLevel.DEBUG;
        } else if (upperContent.contains("TRACE") || upperContent.contains("\"level\":\"TRACE\"")) {
            return LogLevel.TRACE;
        }

        return LogLevel.INFO;
    }
}