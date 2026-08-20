package COMP3011.Assignment_1.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import COMP3011.Assignment_1.model.UptimeResponse;
import COMP3011.Assignment_1.statistics.RuntimeStatisticsService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final RuntimeStatisticsService statisticsService;

    public AdminController(RuntimeStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
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

}