package vn.vnpay.omni.logviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LogViewerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogViewerApplication.class, args);
    }
}