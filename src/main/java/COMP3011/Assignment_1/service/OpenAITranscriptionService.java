package COMP3011.Assignment_1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import COMP3011.Assignment_1.model.OpenAITranscriptionResponse;

@Service
@Profile("titan")
public class OpenAITranscriptionService implements TranscriptionService {

    private final RestClient restClient;
    private final String apiKey;

    public OpenAITranscriptionService(
            RestClient.Builder restClientBuilder,
            @Value("${OPENAI_API_KEY}") String apiKey) {

        this.restClient = restClientBuilder
                .baseUrl("https://api.openai.com/v1")
                .build();

        this.apiKey = apiKey;
    }

    @Override
    public String transcribe(MultipartFile audioFile) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        bodyBuilder.part("file", audioFile.getResource());
        bodyBuilder.part("model", "gpt-4o-mini-transcribe");
        bodyBuilder.part("response_format", "json");

        try {
            OpenAITranscriptionResponse response = restClient
                    .post()
                    .uri("/audio/transcriptions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(bodyBuilder.build())
                    .retrieve()
                    .body(OpenAITranscriptionResponse.class);

            if (response == null || response.getText() == null) {
                throw new IllegalStateException(
                        "OpenAI returned an empty transcription response.");
            }

            return response.getText();

        } catch (RestClientException exception) {
            throw new IllegalStateException(
                    "The transcription service could not be reached.",
                    exception);
        }
    }

}