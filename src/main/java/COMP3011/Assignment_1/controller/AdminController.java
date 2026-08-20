package COMP3011.Assignment_1.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import COMP3011.Assignment_1.model.ErrorResponse;
import COMP3011.Assignment_1.model.ShutdownResponse;
import COMP3011.Assignment_1.model.UptimeResponse;
import COMP3011.Assignment_1.service.ShutdownService;
import COMP3011.Assignment_1.statistics.RuntimeStatisticsService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final RuntimeStatisticsService statisticsService;
    private final ShutdownService shutdownService;
    private final ConfigurableApplicationContext applicationContext;

    public AdminController(
            RuntimeStatisticsService statisticsService,
            ShutdownService shutdownService,
            ConfigurableApplicationContext applicationContext) {

        this.statisticsService = statisticsService;
        this.shutdownService = shutdownService;
        this.applicationContext = applicationContext;
    }

    @GetMapping("/uptime")
    public UptimeResponse getUptime() {
        Instant serverStart = statisticsService.getServerStart();
        Instant now = Instant.now();

        Duration duration = Duration.between(serverStart, now);

        double uptimeSeconds = duration.getSeconds()
                + duration.getNano() / 1_000_000_000.0;

        return new UptimeResponse(
                serverStart,
                now,
                uptimeSeconds);
    }

    @PostMapping("/shutdown")
    public ResponseEntity<?> shutdown() {

        if (!shutdownService.beginShutdown()) {
            ErrorResponse errorResponse = new ErrorResponse(
                    Instant.now(),
                    409,
                    "Conflict",
                    "Graceful shutdown is already in progress.",
                    "/api/v1/admin/shutdown");

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(errorResponse);
        }

        Thread shutdownThread = new Thread(applicationContext::close);
        shutdownThread.start();

        ShutdownResponse response = new ShutdownResponse("Graceful shutdown requested.");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }

}