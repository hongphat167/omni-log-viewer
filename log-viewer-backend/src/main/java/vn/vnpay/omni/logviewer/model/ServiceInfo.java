package vn.vnpay.omni.logviewer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo {
    private String name;
    private String displayName;
    private LocalDateTime lastModified;
    private String url;
    private boolean active;
}