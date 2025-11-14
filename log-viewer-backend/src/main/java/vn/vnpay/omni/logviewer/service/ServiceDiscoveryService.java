// log-viewer-backend/src/main/java/vn/vnpay/omni/logviewer/service/ServiceDiscoveryService.java
package vn.vnpay.omni.logviewer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vn.vnpay.omni.logviewer.model.ServiceInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceDiscoveryService {

    private final WebClient webClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Mono<List<ServiceInfo>> discoverServices() {
        return webClient.get()
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseDirectoryListing)
                .doOnError(e -> log.error("Error discovering services", e))
                .onErrorReturn(new ArrayList<>());
    }

    private List<ServiceInfo> parseDirectoryListing(String html) {
        List<ServiceInfo> services = new ArrayList<>();

        try {
            Document doc = Jsoup.parse(html);
            Elements rows = doc.select("tr");

            for (Element row : rows) {
                Elements cols = row.select("td");
                if (cols.size() >= 3) {
                    Element nameElement = cols.get(0).selectFirst("a");
                    if (nameElement != null) {
                        String name = nameElement.text().trim();

                        if (name.equals("../") || !name.endsWith("/")) {
                            continue;
                        }

                        name = name.substring(0, name.length() - 1);

                        String lastModifiedStr = cols.get(1).text().trim();
                        LocalDateTime lastModified = parseDateTime(lastModifiedStr);

                        ServiceInfo serviceInfo = ServiceInfo.builder()
                                .name(name)
                                .displayName(formatDisplayName(name))
                                .lastModified(lastModified)
                                .url(name + "/")
                                .active(true)
                                .build();

                        services.add(serviceInfo);
                        log.debug("Discovered service: {}", name);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing directory listing", e);
        }

        return services;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    private String formatDisplayName(String name) {
        return name.replace("-", " ")
                .replace("_", " ")
                .toUpperCase();
    }
}