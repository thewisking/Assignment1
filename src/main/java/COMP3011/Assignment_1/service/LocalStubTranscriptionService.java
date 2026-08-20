package COMP3011.Assignment_1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalStubTranscriptionService implements TranscriptionService {

    @Override
    public String transcribe(MultipartFile audioFile) {
        return "Local placeholder transcription.";
    }

}
