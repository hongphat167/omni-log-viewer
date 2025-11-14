// log-viewer-backend/src/main/java/vn/vnpay/omni/logviewer/controller/LogWebSocketController.java
package vn.vnpay.omni.logviewer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import vn.vnpay.omni.logviewer.service.LogStreamService;

import java.util.Map;

/**
 * The type Log web socket controller.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LogWebSocketController {

    private final LogStreamService logStreamService;

    /**
     * Start stream.
     *
     * @param payload the payload
     */
    @MessageMapping("/stream/start")
    public void startStream(@Payload Map<String, Object> payload) {
        String serviceName = (String) payload.get("serviceName");
        log.info("Received start stream request for service: {}", serviceName);
        logStreamService.startStream(serviceName);
    }

    /**
     * Stop stream.
     *
     * @param payload the payload
     */
    @MessageMapping("/stream/stop")
    public void stopStream(@Payload Map<String, Object> payload) {
        String serviceName = (String) payload.get("serviceName");
        log.info("Received stop stream request for service: {}", serviceName);
        logStreamService.stopStream(serviceName);
    }
}