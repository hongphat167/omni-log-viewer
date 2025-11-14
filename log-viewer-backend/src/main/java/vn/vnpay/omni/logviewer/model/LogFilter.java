package vn.vnpay.omni.logviewer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogFilter {
    private List<String> services;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LogLevel logLevel;
    private String requestId;
    private String sessionId;
    private String username;
    private String path;
    private String searchText;
    private Boolean regex;
    private Integer limit;
}