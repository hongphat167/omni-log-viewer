package vn.vnpay.omni.logviewer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEntry {
    private String id;
    private String version;
    private LocalDateTime timestamp;
    private String requestId;
    private String sessionId;
    private String username;
    private String path;
    private LogLevel logLevel;
    private String message;
    private String jsonContent;
    private String serviceName;
    private String rawLog;
}