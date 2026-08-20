package COMP3011.Assignment_1.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import COMP3011.Assignment_1.model.TranscriptionResult;

@Service
@Profile("local")
public class LocalStubTranscriptionService implements TranscriptionService {

    @Override
    public TranscriptionResult transcribe(MultipartFile audioFile) {
        return new TranscriptionResult(
                "Local placeholder transcription.",
                0,
                0);
    }

}
