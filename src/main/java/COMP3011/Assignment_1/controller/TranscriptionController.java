package COMP3011.Assignment_1.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import COMP3011.Assignment_1.model.TranscriptionResult;
import COMP3011.Assignment_1.service.TranscriptionService;
import COMP3011.Assignment_1.statistics.RuntimeStatisticsService;

@RestController
@RequestMapping("/api/v1")
public class TranscriptionController {

    private final TranscriptionService transcriptionService;
    private final RuntimeStatisticsService statisticsService;

    public TranscriptionController(
            TranscriptionService transcriptionService,
            RuntimeStatisticsService statisticsService) {

        this.transcriptionService = transcriptionService;
        this.statisticsService = statisticsService;
    }

    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String transcribe(@RequestParam("audio") MultipartFile audioFile) {
        TranscriptionResult result = transcriptionService.transcribe(audioFile);

        statisticsService.addTokenUsage(
                result.getInputTokens(),
                result.getOutputTokens());

        return result.getText();
    }

}