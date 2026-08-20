package COMP3011.Assignment_1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import COMP3011.Assignment_1.model.GlobalStatsResponse;
import COMP3011.Assignment_1.statistics.RuntimeStatisticsService;

@RestController
@RequestMapping("/api/v1/global")
public class StatisticsController {

    private final RuntimeStatisticsService statisticsService;

    public StatisticsController(RuntimeStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/stats")
    public GlobalStatsResponse getGlobalStats() {
        return new GlobalStatsResponse(
                statisticsService.getInputTokens(),
                statisticsService.getOutputTokens());
    }

}